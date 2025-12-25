package org.roldy.gui.button

import buttonRSBackgroundGrayscale
import buttonRSBorder2
import com.badlogic.gdx.graphics.Color
import org.roldy.core.i18n.I18N
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.i18n.localizable

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.smallButton(
    text: () -> I18N.Key,
    init: (@Scene2dDsl KTextButton).(S) -> Unit = {}
): KTextButton {
    val font = gui.font(25) {
        padTop = 0
        padBottom = 0
    }

    val padding = 20f
    lateinit var button: KTextButton
    table(true) { cell->
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
            localizable(text) {
                textButton(
                    it, textButtonStyle(
                        font = font,
                        background = buttonDrawable(
                            drawable = emptyImage(alpha(.2f)),
                            transition =  transition(
                                normalColor = Color.WHITE alpha 0f,
                                pressedColor = Color.WHITE alpha .5f,
                                overColor = Color.WHITE alpha 1f
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