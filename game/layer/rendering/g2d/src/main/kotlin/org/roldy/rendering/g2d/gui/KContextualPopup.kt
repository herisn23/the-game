package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.CursorManager
import org.roldy.rendering.g2d.gui.KContextualPopup.Backgrounds
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class KContextualPopup(
    val backgrounds: Backgrounds
) : InputListener() {
    var followCursor: Boolean = false
    private var currentAnchor: AnchorPosition = AnchorPosition.BOTTOM_RIGHT
    private var isShowing = false
    var hideCursor = false
    var touched: Boolean = false
    var visible: () -> Boolean = { false }
    private val content: KTable = KTable()
    private val container = object : Container<KTable>(content) {
        override fun draw(batch: Batch, parentAlpha: Float) {
            backgrounds.pick(currentAnchor).draw(batch, x, y, width, height)
            super.draw(batch, parentAlpha)
        }

        override fun act(delta: Float) {
            isVisible = visible() && isShowing
            super.act(delta)
        }
    }
    private var targetActor: Actor? = null

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        touched = true
        hide()
        return true
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        touched = false
    }
    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        if(touched) return
        targetActor = event?.listenerActor
        val stage = targetActor?.stage ?: return

        // Add container to stage if not already added
        if (!container.hasParent()) {
            stage.addActor(container)
        }

        container.isVisible = true
        container.toFront()
        isShowing = true
        show(stage)
        // Initial position
        updatePosition(stage)
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
            targetActor?.stage?.let { updatePosition(it) }
        }
        return false
    }

    fun hide() {
        isShowing = false
        container.isVisible = false
        CursorManager.show()
    }

    fun show(stage: Stage) {
        if (!container.hasParent()) {
            stage.addActor(container)
        }
        container.isVisible = true
        container.toFront()
        isShowing = true
    }

    private fun updatePosition(stage: Stage) {
        // Get mouse position in stage coordinates
        val mousePos = stage.screenToStageCoordinates(
            Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        )

        val cursorX = mousePos.x
        val cursorY = mousePos.y

        // Pack to get actual size
        container.pack()

        val tooltipWidth = container.width
        val tooltipHeight = container.height
        val stageWidth = stage.width
        val stageHeight = stage.height

        // Calculate space available on each side of the cursor
        val spaceRight = stageWidth - cursorX - edgeDistance
        val spaceLeft = cursorX - edgeDistance
        val spaceAbove = stageHeight - cursorY - edgeDistance
        val spaceBelow = cursorY - edgeDistance

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
                cursorX + offsetX
            }

            !isRight && spaceLeft >= tooltipWidth + offsetX -> {
                // Fits on left with offset
                cursorX - offsetX - tooltipWidth
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
                cursorY - offsetY - tooltipHeight
            }

            !isBelow && spaceAbove >= tooltipHeight + offsetY -> {
                // Fits above with offset
                cursorY + offsetY
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

    var offsetX = 0f
    var offsetY = 0f

    var edgeDistance = 5f

//    fun updatePosition() {
//        val vec = popup.position()
//        val x = vec.x
//        val y = vec.y
//
//        val popupWidth = popup.width
//        val popupHeight = popup.height
//        val stageWidth = popup.stage.width
//        val stageHeight = popup.stage.height
//
//        // Calculate space available on each side of the cursor
//        val spaceRight = stageWidth - x - edgeDistance
//        val spaceLeft = x - edgeDistance
//        val spaceAbove = stageHeight - y - edgeDistance
//        val spaceBelow = y - edgeDistance
//
//        // Determine horizontal position and direction
//        val isRight = if (spaceRight >= popupWidth + offsetX) {
//            true  // Fits on right
//        } else if (spaceLeft >= popupWidth + offsetX) {
//            false  // Fits on left
//        } else {
//            spaceRight > spaceLeft  // Use side with more space
//        }
//
//        // Determine vertical position and direction
//        val isBelow = if (spaceBelow >= popupHeight + offsetY) {
//            true  // Fits below
//        } else if (spaceAbove >= popupHeight + offsetY) {
//            false  // Fits above
//        } else {
//            spaceAbove > spaceBelow  // Use side with more space
//        }
//
//        // Set anchor based on position relative to cursor

//        popup.background = backgrounds.pick(anchorPosition)
//
//        // Calculate position
//        val posX = if (isRight) {
//            x + offsetX
//        } else if (spaceLeft >= popupWidth + offsetX) {
//            x - offsetX - popupWidth
//        } else if (spaceRight > spaceLeft) {
//            stageWidth - edgeDistance - popupWidth
//        } else {
//            edgeDistance
//        }
//
//        val posY = if (isBelow) {
//            y - offsetY - popupHeight
//        } else if (spaceAbove >= popupHeight + offsetY) {
//            y + offsetY
//        } else if (spaceAbove > spaceBelow) {
//            stageHeight - edgeDistance - popupHeight
//        } else {
//            edgeDistance
//        }
//
//        container.setPosition(posX, posY)
//    }

    fun minWidth(width: Float) {
        container.minWidth(width)
    }

    fun minHeight(height: Float) {
        container.minHeight(height)
    }

    fun content(content: KTable.(KContextualPopup) -> Unit) {
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
fun <C : KContext> Actor.contextualPopup(
    backgrounds: Backgrounds,
    init: context(C) (@Scene2dDsl KContextualPopup).() -> Unit = {}
): KContextualPopup {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return KContextualPopup(backgrounds).apply {
        init()
    }
}