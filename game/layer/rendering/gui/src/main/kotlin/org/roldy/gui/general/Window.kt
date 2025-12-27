package org.roldy.gui.general

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import org.roldy.gui.general.button.squareButton
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.onClick
import org.roldy.rendering.g2d.gui.el.uiWindow


private const val HeaderHeight = 140f
private const val BorderPadding = 25f
private const val MinWidth = 588f
private const val CloseButtonSize = 110f
private const val OrnamentEffectWidth = 512f

@Scene2dDsl
context(gui: org.roldy.gui.GuiContext)
fun <S> UIWidget<S>.window(
    title: org.roldy.gui.TextManager,
    init: context(org.roldy.gui.GuiContext) (@Scene2dDsl org.roldy.rendering.g2d.gui.el.UITable).(Cell<*>) -> Unit = {}
) = uiWindow {
    touchable = Touchable.enabled
    animationDuration = 0.1f
    dragBoxHeight = HeaderHeight
    dragBoxRightOffset = CloseButtonSize
    background = _root_ide_package_.org.roldy.gui.generalContainer {
        setMinSize(MinWidth, HeaderHeight)
        var containerWidth = 0f
        this traverse _root_ide_package_.org.roldy.gui.windowHeaderBackground {
            tint(gui.colors.primary) redraw { x, y, width, height, draw ->
                containerWidth = width
                draw(x, y + height - HeaderHeight, width, HeaderHeight)
            }
        } traverse _root_ide_package_.org.roldy.gui.windowHeaderOverlay {
            redraw { x, y, width, _, draw ->
                draw(x, y + height - HeaderHeight, width, HeaderHeight)
            }
        } traverse _root_ide_package_.org.roldy.gui.windowHeaderMiddleGlow { //left side
            tint(gui.colors.secondary) redraw { x, y, width, height, draw ->
                draw(x, y + height - HeaderHeight, width / 2, HeaderHeight)
            }
        } traverse _root_ide_package_.org.roldy.gui.windowHeaderMiddleGlow { //right side
            tint(gui.colors.secondary) redraw { x, y, width, height, draw ->
                draw(x + width, y + height - HeaderHeight, -width / 2, HeaderHeight)
            }
        } traverse _root_ide_package_.org.roldy.gui.windowHeaderOrnamentEffect { //left side
            tint(gui.colors.secondary) redraw { x, y, _, height, draw ->
                draw(x, y + height - HeaderHeight, OrnamentEffectWidth, HeaderHeight)
            }
        } traverse _root_ide_package_.org.roldy.gui.windowHeaderOrnamentEffect { //right side
            tint(gui.colors.secondary) redraw { x, y, _, height, draw ->
                draw(x + containerWidth, y + height - HeaderHeight, -OrnamentEffectWidth, HeaderHeight)
            }
        } traverse _root_ide_package_.org.roldy.gui.windowHeaderOrnament { //left side
            redraw { x, y, _, height, draw ->
                draw(x, y + height - HeaderHeight, containerWidth / 2, HeaderHeight)
            }
        } traverse _root_ide_package_.org.roldy.gui.windowHeaderOrnament { //right side
            patch.scale(-1f, 1f)
            redraw { x, y, _, height, draw ->
                draw(x + containerWidth, y + height - HeaderHeight, -containerWidth / 2, HeaderHeight)
            }
        } traverse _root_ide_package_.org.roldy.gui.windowHeaderBorder {
            redraw { x, y, width, height, draw ->
                draw(x - 20f, y + height - HeaderHeight - BorderPadding + 10, width + 40f, 44f)
            }
        } traverse _root_ide_package_.org.roldy.gui.generalContainerBorder2 {
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
                this@uiWindow.close()
            }
        }
    }
    content {
        pad(BorderPadding)
//        padTop(HeaderHeight + BorderPadding)
        init(it)
    }
    pack()
    setPosition(
        (stage.width - width) / 2f,
        (stage.height -height) / 2f
    )
}


