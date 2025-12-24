package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.Vector2Int
import org.roldy.core.x
import org.roldy.rendering.g2d.gui.KPopup.Backgrounds
import org.roldy.rendering.g2d.gui.KPopup.PopupAnchorPosition.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.Delegates

@Scene2dDsl
class KPopup<S>(
    val backgrounds: Backgrounds
) : Container<Stack>(Stack()) {
    private val table = KTable()
    var followPosition: () -> Vector2Int = { 0 x 0 }

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

    private val tmp = Vector2()
    private val anchorGroup = Group()

    var offsetX = 0f
    var offsetY = 0f

    var edgeDistance = 5f

    var padding by Delegates.observable(0f) { _, _, newValue ->
        this.table.pad(newValue)
    }

    init {
        this.isVisible = false
        this.touchable = Touchable.disabled
        this.actor.addActor(table)
        this.actor.addActor(anchorGroup)
    }

    override fun act(delta: Float) {
        super.act(delta)
        updatePosition()
    }

    private fun updatePosition() {
        val screen = followPosition()
        val vec = stage.screenToStageCoordinates(tmp.set(screen.x.toFloat(), screen.y.toFloat()))
        val x = vec.x
        val y = vec.y

        table.pack()
        actor.pack()
        validate()
        pack()

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

    @Scene2dCallbackDsl
    fun show(position: Vector2Int, content: KTable.() -> Unit) {
        showAt(position)

        toFront()
        table.clear()
        table.content()
        pack()
    }

    // Helper function to show popup at mouse/touch coordinates
    fun showAt(screen: Vector2Int) {
        followPosition = { screen }
        if (stage != null) {
            // Convert screen coords to stage coords
            updatePosition()
            this.isVisible = true
        }
    }
}

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <C : KContext, S> KWidget<S>.popup(
    backgrounds: Backgrounds,
    init: context(C) (@Scene2dDsl KPopup<S>).(S) -> Unit = {}
): KPopup<S> {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KPopup(backgrounds), init)
}