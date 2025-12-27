package org.roldy.gui.general.button

import com.badlogic.gdx.graphics.Color
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.gui.buttonRSBackgroundGrayscale
import org.roldy.gui.buttonRSBorder2
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.anim.Normal
import org.roldy.rendering.g2d.gui.anim.Over
import org.roldy.rendering.g2d.gui.anim.Pressed
import org.roldy.rendering.g2d.gui.anim.transition
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
                        transition = transition(
                            colors = mapOf(
                                Normal to (Color.WHITE alpha 0f),
                                Pressed to (Color.WHITE alpha .5f),
                                Over to (Color.WHITE alpha 1f)
                            )
                        )
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