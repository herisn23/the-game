package org.roldy.gui.general.button

import com.badlogic.gdx.graphics.Color
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.gui.buttonRSBackgroundGrayscale
import org.roldy.gui.buttonRSBorder2
import org.roldy.rendering.g2d.FontStyle
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.anim.alphaAnimation
import org.roldy.rendering.g2d.gui.anim.colorAnimation
import org.roldy.rendering.g2d.gui.anim.noneAnimation
import org.roldy.rendering.g2d.gui.anim.stackedAnimation
import org.roldy.rendering.g2d.gui.el.UITextButton
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.table
import org.roldy.rendering.g2d.gui.el.textButton
import org.roldy.rendering.g2d.pixmap

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.smallButton(
    text: TextManager,
    fontColor: Color = gui.colors.button,
    init: (@Scene2dDsl UITextButton).(S) -> Unit = {}
): TextButtonActions =
    smallButton(fontColor = fontColor) {
        setText(text.getText)
        init(it)
    }


@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.smallButton(
    text: String? = null,
    fontColor: Color = gui.colors.button,
    init: (@Scene2dDsl UITextButton).(S) -> Unit = {}
): TextButtonActions {
    val font = gui.font(FontStyle.Default, 50) {
        padTop = 0
        padBottom = 0
        color = fontColor
    }

    val padding = 10f
    lateinit var button: UITextButton
    table { storage ->
        pad(padding)
        textButton(
            text, font
        ) { cell ->
            padLeft(padding * 7)
            padRight(padding * 7)
            padTop(-padding)
            padBottom(-padding)

            val background = colorAnimation(buttonRSBackgroundGrayscale {
                cell.minWidth(minWidth).minHeight(minHeight)
                pad(-padding * 2)
            }) {
                color = gui.colors.primary
                Disabled have gui.colors.disabled
            }
            val border = noneAnimation(buttonRSBorder2 { pad(-3f) })

            val hover = alphaAnimation(pixmap(alpha(.2f)).pad(-padding / 2)) {
                Normal have 0f
                Pressed have .4f
                Over have 1f
                Disabled have 0f
            }

            graphics {
                stackedAnimation {
                    add(background)
                    add(border)
                    add(hover)
                }
            }
            init(storage)
            button = this
        }
    }
    return TextButtonActions(button, gui)
}