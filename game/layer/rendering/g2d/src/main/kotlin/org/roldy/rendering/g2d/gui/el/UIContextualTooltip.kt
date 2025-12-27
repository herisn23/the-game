package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.CursorManager
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext
import org.roldy.rendering.g2d.gui.el.UIContextualTooltip.Backgrounds
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class UIContextualTooltip(
    val backgrounds: Backgrounds,
) : InputListener() {
    var animate = false
    var followCursor: Boolean = false
    private var currentAnchor: AnchorPosition = AnchorPosition.BOTTOM_RIGHT
    private var isShowing = false
    var hideCursor = false
    var touched: Boolean = false
    var visible: () -> Boolean = { false }
    var animationDuration = .1f
    var offsetX = 0f
    var offsetY = 0f
    var edgeDistance = 5f

    private val childrenAnimationMultiplier = .8f
    private val content: UITable = UITable()
    private val container = object : Container<UITable>(content) {
        override fun draw(batch: Batch, parentAlpha: Float) {
            batch.color = color
            backgrounds.pick(currentAnchor).draw(batch, x, y, width * scaleX, height * scaleY)
            super.draw(batch, parentAlpha)
        }
    }

    init {
        container.touchable = Touchable.disabled
        container.isVisible = false
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        touched = true
        hide()
        return true
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        touched = false
    }

    override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        if (!container.hasParent()) {
            event.listenerActor.stage.addActor(container)
        }
        if (touched) return
        show()
        // Initial position
        updatePosition(event.listenerActor)
        if (hideCursor && visible()) {
            CursorManager.hide()
        }
    }

    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        if (hideCursor) {
            CursorManager.show()
        }
        hide()
    }

    override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
        if (followCursor && isShowing) {
            updatePosition()
        }
        return false
    }

    fun hide() {
        if (!visible()) return
        fun finish() {
            container.isTransform = false
            container.isVisible = false
            isShowing = false
        }
        if(animate) {
            container.isTransform = true
            container.setScale(1f)
            container.clearActions()
            container.addAction(
                Actions.parallel(
                    Actions.run {
                        content.addAction(Actions.fadeOut(animationDuration * childrenAnimationMultiplier)) // 2x faster
                    },
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

    fun show() {
        if (!visible()) return
        isShowing = true
        container.toFront()
        container.isVisible = true
        fun finish() {
            container.isTransform = false
        }
        if(animate) {
            container.isTransform = true
            container.setScale(0f)
            container.clearActions()
            container.color.a = 0f  // Start transparent
            container.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.scaleTo(1f, 1f, animationDuration),
                        Actions.fadeIn(animationDuration)
                    ),
                    Actions.run {
                        content.addAction(Actions.fadeIn(animationDuration * childrenAnimationMultiplier)) // 2x faster
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

    private fun updatePosition() {
        // Get mouse position in stage coordinates
        val mousePos = container.stage.screenToStageCoordinates(
            Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        )
        updatePosition(mousePos.x, mousePos.y)
    }

    private fun updatePosition(actor: Actor) {
        val pos = actor.localToStageCoordinates(
            Vector2()
        )
        val width = actor.width
        val height = actor.height
        updatePosition(pos.x + width / 2, pos.y + height / 2)
    }

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

        // Determine horizontal position and direction
        val isRight = if (spaceRight >= tooltipWidth + offsetX) {
            true  // Fits on right
        } else if (spaceLeft >= tooltipWidth + offsetX) {
            false  // Fits on left
        } else {
            spaceRight > spaceLeft  // Use side with more space
        }

        // Determine vertical position and direction
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

        // Calculate final position
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

    enum class AnchorPosition {
        TOP_LEFT,      // Tooltip is right and below cursor
        TOP_RIGHT,     // Tooltip is left and below cursor
        BOTTOM_LEFT,   // Tooltip is right and above cursor
        BOTTOM_RIGHT   // Tooltip is left and above cursor
    }

    fun minWidth(width: Float) {
        container.minWidth(width)
    }

    fun minHeight(height: Float) {
        container.minHeight(height)
    }

    fun content(content: UITable.(UIContextualTooltip) -> Unit) {
        this.content.content(this)
        this.content.pack()
    }

    fun clean() {
        content.clear()
    }
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <C : UIContext> contextualTooltip(
    backgrounds: Backgrounds,
    init: context(C) (@Scene2dDsl UIContextualTooltip).() -> Unit = {}
): UIContextualTooltip {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return UIContextualTooltip(backgrounds).apply {
        init()
    }
}