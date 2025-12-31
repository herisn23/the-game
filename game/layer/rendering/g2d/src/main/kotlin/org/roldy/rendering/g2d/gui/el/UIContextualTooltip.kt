package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import org.roldy.core.*
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.UIContextualTooltip.Drawables
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.Delegates
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

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
 * @param drawables Four different background drawables for each anchor position
 * @param stage The stage to add the tooltip to
 */
@Scene2dDsl
class UIContextualTooltip(
    val drawables: Drawables,
    val stage: UIStage
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
    private var closing = false

    /** Whether the tooltip currently is pinned (mouse enter on tooltip when pinnable) */
    private var pinned: Boolean by Delegates.observable(false) { _, _, newValue ->
        if (newValue) {
            container.touchable = Touchable.enabled
        } else {
            container.touchable = Touchable.disabled
        }
    }

    private var padding = Padding()


    private var focused = false

    private var pinPrepareDuration = 1.seconds

    private var delayCloseDuration = 50.milliseconds

    private var pinScaleY = 0f

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

    private val pinProgressAction
        get() = Actions.sequence(
            floatAction(0f, 1f, pinPrepareDuration) {
                pinScaleY = it
            },
            action {
                pinned = true
            }
        )

    /** Container with custom draw logic to render background with proper transformations */
    private val container = object : Container<UIContextualTooltipContent>(content) {
        override fun draw(batch: Batch, parentAlpha: Float) {
            // Apply transformations to batch for scale/rotation animations
            applyTransform(batch, computeTransform())


            //start draw pin progress from .3f


            drawables.background.draw(batch, 0f, 0f, width, height)
            if (pinScaleY > 0.1f)
                drawables.pinProgressBackground.draw(batch, 0f, 0f, width, height * pinScaleY)
            if (pinned) {
                drawables.pinBorder?.draw(batch, 0f, 0f, width, height)
            }
            // Draw anchors matching current anchor position
            drawables.pickAnchor(currentAnchor)?.draw(batch, 0f, 0f, width, height)
            // Reset batch transformation
            resetTransform(batch)

            // Draw children with transformations
            super.draw(batch, parentAlpha)
        }
    }

    /** Name for debugging purposes */
    var name by Delegates.observable("") { _, _, new ->
        container.name = new
    }

    private val tmpCoords = Vector2()

    init {
        name = "UIContextualTooltip"
        container.touchable = Touchable.disabled
        container.isVisible = false
        content.name = "UIContextualTooltipContent"
        stage.addActor(container)

        // Handle interaction with the tooltip container itself
        container.addInputListener {
            // Block touch events from propagating through tooltip
            block(eventTouchUp, eventTouchDown)
            type(eventEnter) {
                focused = true
            }
            type(eventExit) {
                // Only hide if we're actually leaving the tooltip (not moving to a container)
                whenNotDescendantOf(container) {
                    event.toCoordinates(event.listenerActor, tmpCoords)
                    val hit = stage.cursorActor()
                    val cursorActorIsNotThis = hit?.let {
                        nestedTooltip?.let {
                            hit.isDescendantOf(it.container)
                        } ?: false
                    } ?: false
                    // Keep visible when cursor is on nested tooltip
                    if (cursorActorIsNotThis)
                        return@whenNotDescendantOf

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
        container.isTransform = true
        container.setOrigin(Align.center)
    }

    private fun preparePin() {
        if (nestedTooltip != null && !pinned) {
            container.addAction(pinProgressAction)
        }

    }

    private fun delayClose() {
        closing = true
        container.addAction(delay(delayCloseDuration) {
            closing = false
            if (!focused)
                hide()
        })
    }

    /**
     * Handle touch down events. Marks tooltip as touched and hides it.
     * Returns true to consume the event and prevent propagation.
     */
    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        touched = true
        hide()
        return true
    }

    /**
     * Handle touch up events. Clears the touched flag.
     */
    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        touched = false
    }

    /**
     * Called when mouse enters the actor that has this listener attached.
     * Shows the tooltip and positions it relative to the actor.
     */
    override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        if (touched || content.children.isEmpty) return

        // Position tooltip relative to the actor being hovered
        updatePosition(event.listenerActor)

        show()

        if (hideCursor && visible()) {
            CursorManager.hide()
        }
        preparePin()
    }

    /**
     * Called when mouse exits the actor.
     * Hides the tooltip unless in interaction mode or nested tooltip is visible.
     */
    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        // Keep visible if in interaction mode or nested tooltip is showing
        if (hideCursor) {
            CursorManager.show()
        }
        delayClose()
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
        if (!visible()) return

        if (animate) {
            // Animate scale and fade out
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
                                reset()
                            }
                        )
                    )
                )
            )
        } else {
            reset()
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
        if (animate) {
            // Animate scale and fade in
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
                    }
                )
            )
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
    class Drawables(
        val background: Drawable,
        val pinBorder: Drawable? = null,
        val anchorTopLeft: Drawable? = null,
        val anchorTopRight: Drawable? = null,
        val anchorBottomLeft: Drawable? = null,
        val anchorBottomRight: Drawable? = null,
        val pinProgressBackground: Drawable = emptyImage(Color.WHITE)
    ) {
        fun pickAnchor(position: AnchorPosition) =
            when (position) {
                TOP_LEFT -> anchorTopLeft
                TOP_RIGHT -> anchorTopRight
                BOTTOM_LEFT -> anchorBottomLeft
                BOTTOM_RIGHT -> anchorBottomRight
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
        this.content.clear()
        this.content.pad(padding.top, padding.left, padding.bottom, padding.right)
        this.content.content(this)
        this.content.pack()
    }

    /** Clears all content from the tooltip */
    fun clean() {
        content.clear()
        reset()
    }

    fun reset() {
        pinScaleY = 0f
        container.isVisible = false
        isShowing = false
        container.clearActions()
        pinned = false
        focused = false
    }

    data class Padding(
        var left: Float = 0f,
        var top: Float = 0f,
        var right: Float = 0f,
        var bottom: Float = 0f
    )

    fun pad(uniform: Float) =
        pad(uniform, uniform, uniform, uniform)

    fun pad(left: Float = 0f, top: Float = 0f, right: Float = 0f, bottom: Float = 0f) {
        this.padding = Padding(left, top, right, bottom)
    }

}

/**
 * DSL builder function for creating a contextual tooltip.
 *
 * @param drawables Four background drawables for different anchor positions
 * @param init Initialization lambda for configuring the tooltip
 * @return The created UIContextualTooltip instance
 */
@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(context: C)
fun <C : UIContext> contextualTooltip(
    drawables: Drawables,
    init: context(C) (@Scene2dDsl UIContextualTooltip).() -> Unit = {}
): UIContextualTooltip {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return UIContextualTooltip(drawables, context.stage()).apply {
        init()
    }
}