package org.roldy.gui.general

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.utils.Align
import org.roldy.gui.*
import org.roldy.gui.general.button.squareButton
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.*
import org.roldy.rendering.g2d.gui.redraw
import org.roldy.rendering.g2d.gui.traverse


private const val HeaderHeight = 140f
private const val BorderPadding = 25f
private const val MinWidth = 588f
private const val CloseButtonSize = 110f
private const val OrnamentEffectWidth = 512f


class WindowActions(
    private val window: UIWindow,
    val title: LabelActions,
    val gui: GuiContext
) {

    fun close(onComplete: (() -> Unit)? = null) {
        window.close(onComplete)
    }

    fun open(onComplete: (() -> Unit)? = null) {
        window.open(onComplete)
    }

    fun rebuild() {
        window.pack()
    }

    fun toggle(onComplete: (() -> Unit)? = null) {
        window.toggle(onComplete)
    }

    fun setPosition(x: Float, y: Float, alignment: Int = Align.bottomLeft) {
        window.setPosition(x, y, alignment)
    }
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.window(
    text: TextManager,
    name: String = "Window",
    visible: Boolean = false,
    init: context(GuiContext) (@Scene2dDsl UITable).(Cell<*>) -> Unit = {}
): WindowActions =
    window(null, name, visible, init).apply {
        title.setText(text)
    }

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.window(
    title: String? = null,
    name: String = "Window",
    visible: Boolean = false,
    init: context(GuiContext) (@Scene2dDsl UITable).(Cell<*>) -> Unit = {}
): WindowActions {
    lateinit var titleLabel: LabelActions
    val window = uiWindow {
        this.isVisible = visible
        this.name = name
        touchable = Touchable.enabled //block events to prevent touching behind window
        animationDuration = .0f
        dragBoxHeight = HeaderHeight
        dragBoxRightOffset = CloseButtonSize
        background = generalContainer {
            setMinSize(MinWidth, HeaderHeight)
            var containerWidth = 0f
            this traverse windowHeaderBackground {
                tint(gui.colors.primary) redraw { x, y, width, height, draw ->
                    containerWidth = width
                    draw(x, y + height - HeaderHeight, width, HeaderHeight)
                }
            } traverse windowHeaderOverlay {
                redraw { x, y, width, _, draw ->
                    draw(x, y + height - HeaderHeight, width, HeaderHeight)
                }
            } traverse windowHeaderMiddleGlow { //left side
                tint(gui.colors.secondary) redraw { x, y, width, height, draw ->
                    draw(x, y + height - HeaderHeight, width / 2, HeaderHeight)
                }
            } traverse windowHeaderMiddleGlow { //right side
                tint(gui.colors.secondary) redraw { x, y, width, height, draw ->
                    draw(x + width, y + height - HeaderHeight, -width / 2, HeaderHeight)
                }
            } traverse windowHeaderOrnamentEffect { //left side
                tint(gui.colors.secondary) redraw { x, y, _, height, draw ->
                    draw(x, y + height - HeaderHeight, OrnamentEffectWidth, HeaderHeight)
                }
            } traverse windowHeaderOrnamentEffect { //right side
                tint(gui.colors.secondary) redraw { x, y, _, height, draw ->
                    draw(x + containerWidth, y + height - HeaderHeight, -OrnamentEffectWidth, HeaderHeight)
                }
            } traverse windowHeaderOrnament { //left side
                redraw { x, y, _, height, draw ->
                    draw(x, y + height - HeaderHeight, containerWidth / 2, HeaderHeight)
                }
            } traverse windowHeaderOrnament { //right side
                patch.scale(-1f, 1f)
                redraw { x, y, _, height, draw ->
                    draw(x + containerWidth, y + height - HeaderHeight, -containerWidth / 2, HeaderHeight)
                }
            } traverse windowHeaderBorder {
                redraw { x, y, width, height, draw ->
                    draw(x - 20f, y + height - HeaderHeight - BorderPadding + 10, width + 40f, 44f)
                }
            } traverse generalContainerBorder2 {
                redraw { x, y, width, height, draw ->
                    draw(x - BorderPadding, y - BorderPadding, width + BorderPadding * 2, height + BorderPadding * 2)
                }
            }
        }
        //header
        header {
            it.top().pad(10f)
            label(title ?: "Window", 60) {
                titleLabel = this
                it.center().expand().padRight(-CloseButtonSize - 7)
                label.color = gui.colors.window
            }
            squareButton(gui.drawable { Ico_X }) { cell ->
                onClick {
                    this@uiWindow.close()
                }
            }
        }
        content {
            pad(BorderPadding)
            init(it)
        }
        pack()
        //default position to center
        setPosition(
            stage.width / 2f,
            stage.height / 2f,
            Align.center
        )
    }
    return WindowActions(window, titleLabel, gui)
}

