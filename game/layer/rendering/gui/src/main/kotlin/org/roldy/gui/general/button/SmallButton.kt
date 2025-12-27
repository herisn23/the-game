package org.roldy.gui.general.button

import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.gui.buttonRSBackgroundGrayscale
import org.roldy.gui.buttonRSBorder2
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.Normal
import org.roldy.rendering.g2d.gui.Over
import org.roldy.rendering.g2d.gui.Pressed
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.*

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.smallButton(
    text: TextManager,
    init: (@Scene2dDsl UITextButton).(S) -> Unit = {}
): UITextButton {
    val font = gui.font(25) {
        padTop = 0
        padBottom = 0
    }

    val padding = 20f
    lateinit var button: UITextButton
    table(true) { cell ->
        //background
        buttonRSBackgroundGrayscale {
            image(this) {
                color = gui.colors.primary
            }
        }
        table(true) {
            pad(padding)
            //border
            buttonRSBorder2 {
                image(this)
            }
        }
        table(true) {
            pad(padding - 3) //pad to parent
            text { string ->
                textButton(
                    string, textButtonStyle(
                        font = font,
                        background = emptyImage(alpha(.2f)),
                        transition = {
                            Normal to 0f
                            Pressed to .4f
                            Over to 1f
                        }
                    )
                ) {
                    padLeft(padding)
                    padRight(padding)
                    init(cell)
                    button = this
                }
            }
        }
    }
    return button
}