package org.roldy.gui

import com.badlogic.gdx.utils.Align
import generalContainer
import generalContainerBorder2
import org.roldy.gui.button.squareButton
import org.roldy.rendering.g2d.gui.*
import windowHeaderBackground
import windowHeaderBorder
import windowHeaderMiddleGlow
import windowHeaderOrnament
import windowHeaderOrnamentEffect
import windowHeaderOverlay


private const val HeaderHeight = 140f
private const val BorderPadding = 25f
private const val MinWidth = 588f
private const val CloseButtonSize = 110f
private const val OrnamentEffectWidth = 512f

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.guiWindow(
    title: TextManager,
    init: context(GuiContext) (@Scene2dDsl KTable).(S) -> Unit = {}
) = window { cell ->
    animationDuration = 0.0f
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
        pad(10f)
        label(title, 60) {
            it.center().expand().padRight(-CloseButtonSize - 7)
            color = gui.colors.window
        }
        squareButton(gui.drawable { Ico_X }) { cell ->
            onClick {
                this@window.close()
            }
        }
    }
    content {
        pad(BorderPadding)
        padTop(HeaderHeight + BorderPadding)
        align(Align.top)
        init(cell)

    }
    pack()
    setPosition(
        (stage.width - width) / 2f,
        (stage.height -height) / 2f
    )
}


