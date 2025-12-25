package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.rendering.g2d.gui.KClampingPopup.Backgrounds
import org.roldy.rendering.g2d.gui.KClampingPopup.PopupAnchorPosition.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class KClampingPopup(
    val backgrounds: Backgrounds
) : KPopup() {
    class Backgrounds(
        val topLeft: Drawable,
        val topRight: Drawable,
        val bottomLeft: Drawable,
        val bottomRight: Drawable
    ) {
        fun pick(position: PopupAnchorPosition) =
            when (position) {
                TOP_LEFT -> topLeft
                TOP_RIGHT -> topRight
                BOTTOM_LEFT -> bottomLeft
                BOTTOM_RIGHT -> bottomRight
            }
    }

    enum class PopupAnchorPosition {
        TOP_LEFT,      // Popup is right and below cursor
        TOP_RIGHT,     // Popup is left and below cursor
        BOTTOM_LEFT,   // Popup is right and above cursor
        BOTTOM_RIGHT   // Popup is left and above cursor
    }

    var offsetX = 0f
    var offsetY = 0f

    var edgeDistance = 5f

    override fun updatePosition() {
        val vec = calculatePosition()
        val x = vec.x
        val y = vec.y

        val popupWidth = width
        val popupHeight = height
        val stageWidth = stage.width
        val stageHeight = stage.height

        // Calculate space available on each side of the cursor
        val spaceRight = stageWidth - x - edgeDistance
        val spaceLeft = x - edgeDistance
        val spaceAbove = stageHeight - y - edgeDistance
        val spaceBelow = y - edgeDistance

        // Determine horizontal position and direction
        val isRight = if (spaceRight >= popupWidth + offsetX) {
            true  // Fits on right
        } else if (spaceLeft >= popupWidth + offsetX) {
            false  // Fits on left
        } else {
            spaceRight > spaceLeft  // Use side with more space
        }

        // Determine vertical position and direction
        val isBelow = if (spaceBelow >= popupHeight + offsetY) {
            true  // Fits below
        } else if (spaceAbove >= popupHeight + offsetY) {
            false  // Fits above
        } else {
            spaceAbove > spaceBelow  // Use side with more space
        }

        // Set anchor based on position relative to cursor
        val anchorPosition = when {
            isRight && isBelow -> TOP_LEFT
            !isRight && isBelow -> TOP_RIGHT
            isRight && !isBelow -> BOTTOM_LEFT
            else -> BOTTOM_RIGHT
        }
        background = backgrounds.pick(anchorPosition)

        // Calculate position
        val posX = if (isRight) {
            x + offsetX
        } else if (spaceLeft >= popupWidth + offsetX) {
            x - offsetX - popupWidth
        } else if (spaceRight > spaceLeft) {
            stageWidth - edgeDistance - popupWidth
        } else {
            edgeDistance
        }

        val posY = if (isBelow) {
            y - offsetY - popupHeight
        } else if (spaceAbove >= popupHeight + offsetY) {
            y + offsetY
        } else if (spaceAbove > spaceBelow) {
            stageHeight - edgeDistance - popupHeight
        } else {
            edgeDistance
        }

        setPosition(posX, posY)
    }

}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <C : KContext, S> KWidget<S>.clampingPopup(
    backgrounds: Backgrounds,
    init: context(C) (@Scene2dDsl KClampingPopup).(S) -> Unit = {}
): KClampingPopup {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KClampingPopup(backgrounds), init)
}