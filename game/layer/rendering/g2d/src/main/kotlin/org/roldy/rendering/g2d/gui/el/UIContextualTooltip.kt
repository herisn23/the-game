package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import org.roldy.core.*
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.UIContextualTooltip.Backgrounds
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.Delegates

/**
 * Table container for the actual tooltip content.
 * Acts as the content holder within the UIContextualTooltip system.
 */
class UIContextualTooltipContent(
    val root: UIContextualTooltip
) : Table(), UITableWidget

/**
 * A contextual tooltip system that displays information near the cursor or actor.
 *
 * Features:
 * - Automatic positioning with smart anchor placement to stay within screen bounds
 * - Optional cursor following mode
 * - Optional show/hide animations with scale and fade effects
 * - Interactive mode: Hold Alt key to make tooltip interactable (clickable, hoverable)
 * - Nested tooltip support: Tooltips can contain other tooltips
 * - Custom backgrounds for each anchor position (4 corners)
 * - Cursor hiding support
 *
 * Positioning:
 * The tooltip automatically positions itself relative to the cursor/actor and chooses
 * one of four anchor positions (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT) based
 * on available screen space. The background changes to match the anchor position.
 *
 * Interaction Model:
 * - By default, tooltips are non-interactive and disappear on mouse exit
 * - Press Alt to enable interaction mode (tooltip becomes clickable/hoverable)
 * - While Alt is held or tooltip is focused, it remains visible
 * - Supports nested tooltips that keep parent visible
 *
 * @param backgrounds Four different background drawables for each anchor position
 * @param stage The stage to add the tooltip to
 */
@Scene2dDsl
class UIContextualTooltip(
    val backgrounds: Backgrounds,
    val stage: Stage
) : InputListener() {

    // Animation settings
    /** Enable scale and fade animations when showing/hiding */
    var animate = false

    /** Animation duration in seconds for show/hide transitions */
    var animationDuration = .1f

    /** Multiplier for content fade animation (makes content fade faster than container) */
    private val childrenAnimationMultiplier = .8f

    // Positioning settings
    /** If true, tooltip follows the cursor in real-time */
    var followCursor: Boolean = false

    /** Horizontal offset from the cursor/actor position */
    var offsetX = 0f

    /** Vertical offset from the cursor/actor position */
    var offsetY = 0f

    /** Minimum distance from screen edges */
    var edgeDistance = 5f

    /** Current anchor position determining which corner is attached to cursor */
    private var currentAnchor: AnchorPosition = AnchorPosition.BOTTOM_RIGHT

    // State tracking
    /** Whether the tooltip is currently visible */
    private var isShowing = false

    /** Whether interaction mode is active (Alt key held or tooltip focused) */
    private var interaction = false

    /** Whether the tooltip currently has focus (mouse is over it) */
    private var focused = false

    /** Whether a touch/click is currently active on this tooltip */
    var touched: Boolean = false

    // Cursor management
    /** If true, hides the system cursor when tooltip is visible */
    var hideCursor = false

    // Visibility control
    /** Lambda that determines if tooltip should be shown (for conditional visibility) */
    var visible: () -> Boolean = { true }

    // Nested tooltip support
    /** Reference to parent tooltip if this is nested */
    private var parentTooltip: UIContextualTooltip? = null

    /** Reference to child tooltip nested within this one */
    var nestedTooltip: UIContextualTooltip? by Delegates.observable(null) { _, _, new ->
        new?.parentTooltip = this
    }

    // UI components
    private val content: UIContextualTooltipContent = UIContextualTooltipContent(this)

    /** Container with custom draw logic to render background with proper transformations */
    private val container = object : Container<UIContextualTooltipContent>(content) {
        override fun draw(batch: Batch, parentAlpha: Float) {
            validate()

            // Apply transformations to batch for scale/rotation animations
            applyTransform(batch, computeTransform())

            // Draw background matching current anchor position
            batch.color = color
            backgrounds.pick(currentAnchor).draw(batch, 0f, 0f, width, height)

            // Reset batch transformation
            resetTransform(batch)

            // Draw children with transformations
            super.draw(batch, parentAlpha)
        }
    }

    /** Name for debugging purposes */
    var name by Delegates.observable("") { _, old, new ->
        container.name = new
    }

    init {
        name = "UIContextualTooltip"
        container.touchable = Touchable.disabled
        container.isVisible = false
        content.name = "UIContextualTooltipContent"
        stage.addActor(container)

        // Set up Alt key interaction mode
        // When Alt is pressed, tooltip becomes interactable (can click/hover without hiding)
        stage.addInputListener {
            key(eventKeyDown, keyAltLeft, keySym) {
                interaction = true
                container.touchable = Touchable.enabled
            }
            key(eventKeyUp, keyAltLeft, keySym) {
                if (!focused) {
                    interaction = false
                    container.touchable = Touchable.disabled
                }
            }
        }

        // Handle interaction with the tooltip container itself
        container.addInputListener {
            // Block touch events from propagating through tooltip
            block(eventTouchUp, eventTouchDown)

            type(eventEnter) {
                focused = true
            }

            type(eventExit) {
                // Only hide if we're actually leaving the tooltip (not moving to a child)
                whenNotDescendantOf(container) {
                    // Keep visible if nested tooltip is showing
                    if (nestedTooltip?.isShowing == true)
                        return@whenNotDescendantOf
                    event.relatedActor?.isDescendantOf(container)
                    interaction = false
                    focused = false
                    hide()

                    // When exiting from nested tooltip, check if also exiting parent
                    parentTooltip?.let {
                        whenNotDescendantOf(it.container) {
                            parentTooltip?.hideByNested()
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle touch down events. Marks tooltip as touched and hides it.
     * Returns true to consume the event and prevent propagation.
     */
    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        touched = true
        hide()
        println("Touch down on tooltip")
        return true
    }

    /**
     * Handle touch up events. Clears the touched flag.
     */
    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        touched = false
        println("Touch up on tooltip")
    }

    /**
     * Called when mouse enters the actor that has this listener attached.
     * Shows the tooltip and positions it relative to the actor.
     */
    override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        if (touched) return

        // Position tooltip relative to the actor being hovered
        updatePosition(event.listenerActor)

        show()

        if (hideCursor && visible()) {
            CursorManager.hide()
        }
    }

    /**
     * Called when mouse exits the actor.
     * Hides the tooltip unless in interaction mode or nested tooltip is visible.
     */
    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        // Keep visible if in interaction mode or nested tooltip is showing
        if (interaction || nestedTooltip?.isShowing == true) {
            return
        }
        if (hideCursor) {
            CursorManager.show()
        }
        hide()
    }

    /**
     * Called when mouse moves over the actor.
     * Updates tooltip position if cursor following is enabled.
     */
    override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
        if (followCursor && isShowing) {
            updatePosition()
        }
        return false
    }

    /**
     * Recursively hides this tooltip and all parent tooltips in the hierarchy.
     *
     * Called when the user exits a nested tooltip structure entirely (mouse leaves
     * both the nested tooltip AND its parent). This cascades the hide operation
     * up the parent chain, ensuring all tooltips in the hierarchy are hidden together.
     *
     * Execution order: Hides parents first (recursively), then hides self.
     */
    private fun hideByNested() {
        parentTooltip?.hideByNested()
        hide()
    }

    /**
     * Hides the tooltip with optional animation.
     * Disables interaction and restores cursor visibility.
     */
    fun hide() {
        container.touchable = Touchable.disabled
        if (!visible()) return

        fun finish() {
            container.isTransform = false
            container.isVisible = false
            isShowing = false
        }

        if (animate) {
            // Animate scale and fade out
            container.isTransform = true
            setOriginByAnchor()
            container.setScale(1f)
            container.clearActions()
            container.addAction(
                Actions.parallel(
                    // Fade content out first (faster)
                    Actions.run {
                        content.addAction(Actions.fadeOut(animationDuration * childrenAnimationMultiplier))
                    },
                    // Then scale and fade container
                    Actions.sequence(
                        Actions.delay(animationDuration * childrenAnimationMultiplier),
                        Actions.sequence(
                            Actions.parallel(
                                Actions.scaleTo(0f, 0f, animationDuration),
                                Actions.fadeOut(animationDuration)
                            ),
                            Actions.run {
                                finish()
                            }
                        )
                    )
                )
            )
        } else {
            finish()
        }

        CursorManager.show()
    }

    /**
     * Sets the container's origin point based on current anchor position.
     * This ensures scale animations expand/contract from the correct corner.
     */
    fun setOriginByAnchor() {
        when (currentAnchor) {
            AnchorPosition.TOP_LEFT -> container.setOrigin(Align.topLeft)
            AnchorPosition.TOP_RIGHT -> container.setOrigin(Align.topRight)
            AnchorPosition.BOTTOM_LEFT -> container.setOrigin(Align.bottomLeft)
            AnchorPosition.BOTTOM_RIGHT -> container.setOrigin(Align.bottomRight)
        }
    }

    /**
     * Shows the tooltip with optional animation.
     * Brings tooltip to front and makes it visible.
     */
    fun show() {
        if (!visible()) return
        isShowing = true
        container.toFront()
        container.isVisible = true

        fun finish() {
            container.isTransform = false
        }

        if (animate) {
            // Animate scale and fade in
            container.isTransform = true
            setOriginByAnchor()
            container.setScale(0f)
            container.clearActions()
            container.color.a = 0f  // Start transparent
            container.addAction(
                Actions.sequence(
                    // Scale and fade container in
                    Actions.parallel(
                        Actions.scaleTo(1f, 1f, animationDuration),
                        Actions.fadeIn(animationDuration)
                    ),
                    // Then fade content in (faster)
                    Actions.run {
                        content.addAction(Actions.fadeIn(animationDuration * childrenAnimationMultiplier))
                    },
                    Actions.run {
                        finish()
                    }
                )
            )
        } else {
            finish()
        }
    }

    /**
     * Updates tooltip position based on current mouse cursor location.
     */
    private fun updatePosition() {
        // Get mouse position in stage coordinates
        val mousePos = container.stage.screenToStageCoordinates(
            Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        )
        updatePosition(mousePos.x, mousePos.y)
    }

    /**
     * Updates tooltip position relative to the given actor (centered on actor).
     */
    private fun updatePosition(actor: Actor) {
        val pos = actor.localToStageCoordinates(Vector2())
        val width = actor.width
        val height = actor.height
        updatePosition(pos.x + width / 2, pos.y + height / 2)
    }

    /**
     * Updates tooltip position and anchor based on available screen space.
     *
     * Algorithm:
     * 1. Calculate available space on all sides of the cursor position
     * 2. Determine which side (left/right, above/below) has enough space
     * 3. Choose anchor position based on where tooltip will appear relative to cursor
     * 4. Position tooltip with offsets, clamping to screen edges if needed
     *
     * Anchor positioning logic:
     * - Tooltip right+below cursor → TOP_LEFT anchor (tooltip grows down-right from cursor)
     * - Tooltip left+below cursor → TOP_RIGHT anchor (tooltip grows down-left from cursor)
     * - Tooltip right+above cursor → BOTTOM_LEFT anchor (tooltip grows up-right from cursor)
     * - Tooltip left+above cursor → BOTTOM_RIGHT anchor (tooltip grows up-left from cursor)
     *
     * @param x Cursor X position in stage coordinates
     * @param y Cursor Y position in stage coordinates
     */
    private fun updatePosition(x: Float, y: Float) {
        // Pack to get actual size
        container.pack()

        val tooltipWidth = container.width
        val tooltipHeight = container.height
        val stageWidth = container.stage.width
        val stageHeight = container.stage.height

        // Calculate space available on each side of the cursor
        val spaceRight = stageWidth - x - edgeDistance
        val spaceLeft = x - edgeDistance
        val spaceAbove = stageHeight - y - edgeDistance
        val spaceBelow = y - edgeDistance

        // Determine horizontal position: prefer right, fall back to left, or use side with more space
        val isRight = if (spaceRight >= tooltipWidth + offsetX) {
            true  // Fits on right
        } else if (spaceLeft >= tooltipWidth + offsetX) {
            false  // Fits on left
        } else {
            spaceRight > spaceLeft  // Use side with more space
        }

        // Determine vertical position: prefer below, fall back to above, or use side with more space
        val isBelow = if (spaceBelow >= tooltipHeight + offsetY) {
            true  // Fits below
        } else if (spaceAbove >= tooltipHeight + offsetY) {
            false  // Fits above
        } else {
            spaceAbove > spaceBelow  // Use side with more space
        }

        // Determine anchor position based on where tooltip appears relative to cursor
        val anchorPosition = when {
            isRight && isBelow -> AnchorPosition.TOP_LEFT      // Tooltip right+below = anchor at top-left
            !isRight && isBelow -> AnchorPosition.TOP_RIGHT    // Tooltip left+below = anchor at top-right
            isRight && !isBelow -> AnchorPosition.BOTTOM_LEFT  // Tooltip right+above = anchor at bottom-left
            else -> AnchorPosition.BOTTOM_RIGHT                // Tooltip left+above = anchor at bottom-right
        }

        // Update background if anchor changed
        if (anchorPosition != currentAnchor) {
            currentAnchor = anchorPosition
        }

        // Calculate final X position
        val posX = when {
            isRight && spaceRight >= tooltipWidth + offsetX -> {
                // Fits on right with offset
                x + offsetX
            }

            !isRight && spaceLeft >= tooltipWidth + offsetX -> {
                // Fits on left with offset
                x - offsetX - tooltipWidth
            }

            spaceRight > spaceLeft -> {
                // More space on right, clamp to edge
                (stageWidth - edgeDistance - tooltipWidth).coerceAtLeast(edgeDistance)
            }

            else -> {
                // More space on left, clamp to edge
                edgeDistance
            }
        }

        // Calculate final Y position
        val posY = when {
            isBelow && spaceBelow >= tooltipHeight + offsetY -> {
                // Fits below with offset
                y - offsetY - tooltipHeight
            }

            !isBelow && spaceAbove >= tooltipHeight + offsetY -> {
                // Fits above with offset
                y + offsetY
            }

            spaceAbove > spaceBelow -> {
                // More space above, clamp to edge
                (stageHeight - edgeDistance - tooltipHeight).coerceAtLeast(edgeDistance)
            }

            else -> {
                // More space below, clamp to edge
                edgeDistance
            }
        }

        container.setPosition(posX, posY)
    }

    /**
     * Container for four background drawables, one for each anchor position.
     * The appropriate background is selected based on where the tooltip appears relative to the cursor.
     */
    class Backgrounds(
        val topLeft: Drawable,
        val topRight: Drawable,
        val bottomLeft: Drawable,
        val bottomRight: Drawable
    ) {
        fun pick(position: AnchorPosition) =
            when (position) {
                TOP_LEFT -> topLeft
                TOP_RIGHT -> topRight
                BOTTOM_LEFT -> bottomLeft
                BOTTOM_RIGHT -> bottomRight
            }
    }

    /**
     * Anchor position determines which corner of the tooltip is "attached" to the cursor.
     * This affects both the background drawable used and the animation origin point.
     */
    enum class AnchorPosition {
        TOP_LEFT,      // Tooltip is positioned to the right and below cursor
        TOP_RIGHT,     // Tooltip is positioned to the left and below cursor
        BOTTOM_LEFT,   // Tooltip is positioned to the right and above cursor
        BOTTOM_RIGHT   // Tooltip is positioned to the left and above cursor
    }

    /** Sets the minimum width of the tooltip container */
    fun minWidth(width: Float) {
        container.minWidth(width)
    }

    /** Sets the minimum height of the tooltip container */
    fun minHeight(height: Float) {
        container.minHeight(height)
    }

    /**
     * Configures the tooltip content using a DSL builder.
     * @param content Lambda to build the tooltip content table
     */
    fun content(content: UIContextualTooltipContent.(UIContextualTooltip) -> Unit) {
        this.content.content(this)
        this.content.pack()
    }

    /** Clears all content from the tooltip */
    fun clean() {
        content.clear()
    }
}

/**
 * DSL builder function for creating a contextual tooltip.
 *
 * @param backgrounds Four background drawables for different anchor positions
 * @param init Initialization lambda for configuring the tooltip
 * @return The created UIContextualTooltip instance
 */
@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(context: C)
fun <C : UIContext> contextualTooltip(
    backgrounds: Backgrounds,
    init: context(C) (@Scene2dDsl UIContextualTooltip).() -> Unit = {}
): UIContextualTooltip {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return UIContextualTooltip(backgrounds, context.stage()).apply {
        init()
    }
}