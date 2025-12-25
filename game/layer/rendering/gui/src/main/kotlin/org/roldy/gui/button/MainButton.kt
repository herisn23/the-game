package org.roldy.gui.button

import buttonRLBackground
import buttonRLForeground
import buttonRLHover
import buttonRLOverlay1
import buttonRLOverlay2
import com.badlogic.gdx.scenes.scene2d.Touchable
import org.roldy.core.i18n.I18N
import org.roldy.core.utils.alpha
import org.roldy.core.utils.brighter
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.i18n.localizable

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.mainButton(
    text: () -> I18N.Key,
    init: (@Scene2dDsl KTextButton).() -> Unit = {}
): KTextButton {
    val font = gui.font(60) {
        padTop = 0
        padBottom = 0
    }
    val padding = 30f
    lateinit var button: KTextButton
    table(true) {
        //background
        buttonRLBackground {
            image(this)
        }
        table {
            pad(padding)
            buttonRLForeground {
                image(tint(gui.colors.primary))
            }
        }
        table(true) {
            pad(padding + 5)

            localizable(text) { string ->
                textButton(
                    string, textButtonStyle(
                        font = font,
                        background = buttonDrawable(
                            drawable = buttonRLHover {
                                mask(this)

                            },
                            transition = transition(
                                normalColor = gui.colors.primary alpha 0f,
                                pressedColor = gui.colors.primary alpha .5f,
                                overColor = gui.colors.primary alpha 1f
                            )
                        )
                    )
                ) {
                    init()
                    button = this
                }
            }
        }
        table(true) {
            pad(padding)
            buttonRLOverlay1 {
                image(tint(alpha(.7f))) {
                    touchable = Touchable.disabled
                }
            }

            table(true) {
                pad(1f, -1f, 5f, -1f)
                //overlay 2
                buttonRLOverlay2 {
                    image(tint((gui.colors.primary brighter 2f) alpha 0.25f)) {
                        touchable = Touchable.disabled
                    }
                }
            }
        }
    }
    return button
}