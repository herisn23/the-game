package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.UIContext

@org.roldy.rendering.g2d.gui.Scene2dDsl
class UIWindow : Table(), UITableWidget {
    var keepWithinStage = true
    var draggable = true

    // Simplified: just define the Y position and height, width auto-calculates
    var dragBoxTopOffset = 0f      // Distance from top
    var dragBoxHeight = 0f         // Height of draggable area
    var dragBoxLeftOffset = 0f     // Left margin
    var dragBoxRightOffset = 0f    // Right margin (e.g., for close button)
    var animationDuration = 0.1f  // Duration in seconds
    private var isAnimating = false

    override fun drawDebug(shapes: ShapeRenderer) {
        shapes.color = Color.PINK
        // Calculate width based on current window width
        val boxWidth = width - dragBoxLeftOffset - dragBoxRightOffset
        val boxX = x + dragBoxLeftOffset
        val boxY = y + height - dragBoxTopOffset - dragBoxHeight
        shapes.rect(boxX, boxY, boxWidth, dragBoxHeight)
        super.drawDebug(shapes)
    }

    init {
        touchable = Touchable.enabled
        pad(0f)
        addListener(
            object : com.badlogic.gdx.scenes.scene2d.InputListener() {
                var startX: Float = 0f
                var startY: Float = 0f
                var canDrag = false

                override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    toFront()
                    if (button != Input.Buttons.LEFT) return true
                    if (!draggable) return true
                    // Calculate drag box bounds
                    val boxLeft = dragBoxLeftOffset
                    val boxRight = width - dragBoxRightOffset
                    val boxBottom = height - dragBoxTopOffset - dragBoxHeight
                    val boxTop = height - dragBoxTopOffset
                    canDrag = false
                    // Check if touch is within drag box
                    if (dragBoxHeight == .0f || x in boxLeft..boxRight && y in boxBottom..boxTop) {
                        startX = x
                        startY = y
                        canDrag = true
                    }
                    return true
                }

                override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                    if(canDrag)
                    if (keepWithinStage && stage != null) {
                        val newX = this@UIWindow.x + (x - startX)
                        val newY = this@UIWindow.y + (y - startY)

                        val clampedX = newX.coerceIn(0f, stage.width - width)
                        val clampedY = newY.coerceIn(0f, stage.height - height)

                        setPosition(clampedX, clampedY)
                    } else {
                        moveBy(x - startX, y - startY)
                    }
                }
            }
        )
    }

    @Scene2dCallbackDsl
    context(_: C)
    fun <C : UIContext> header(build: @Scene2dCallbackDsl UITable.(Cell<*>) -> Unit) =
        table {
            debug = this@UIWindow.debug
            it.growX().minWidth(0f)
            build(it)
        }.also {
            row()
        }

    @Scene2dCallbackDsl
    context(_: C)
    fun <C : UIContext> content(build: @Scene2dCallbackDsl UITable.(Cell<*>) -> Unit) =
        table {
            debug = this@UIWindow.debug
            padLeft(0f)
            padRight(0f)
//            setFillParent(true)
            build(it)
        }

    fun open(onComplete: (() -> Unit)? = null) {
        if (isAnimating || isVisible) return

        isAnimating = true
        isVisible = true
        touchable = Touchable.disabled  // Disable during animation
        color.a = 0f  // Start transparent

        clearActions()
        addAction(
            Actions.sequence(
                Actions.fadeIn(animationDuration),
                Actions.run {
                    isAnimating = false
                    touchable = Touchable.enabled
                    onComplete?.invoke()
                }
            )
        )
    }

    fun close(onComplete: (() -> Unit)? = null) {
        if (isAnimating || !isVisible) return

        isAnimating = true
        touchable = Touchable.disabled  // Disable during animation

        clearActions()
        addAction(
            Actions.sequence(
                Actions.fadeOut(animationDuration),
                Actions.run {
                    isVisible = false
                    isAnimating = false
                    onComplete?.invoke()
                }
            )
        )
    }

    fun toggle(onComplete: (() -> Unit)? = null) {
        if (isVisible) {
            close(onComplete)
        } else {
            open(onComplete)
        }
    }

//    override fun <T : Actor> add(actor: T): Cell<T> {
//        return this.actor.add(actor)
//    }
}


@org.roldy.rendering.g2d.gui.Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.uiWindow(
    init: context(C) (@org.roldy.rendering.g2d.gui.Scene2dDsl UIWindow).(S) -> Unit = {}
): UIWindow =
    actor(UIWindow()) {
        init(it)
        pack()
    }