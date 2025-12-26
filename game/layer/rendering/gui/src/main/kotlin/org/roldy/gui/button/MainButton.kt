package org.roldy.gui.button

import buttonRLBackground
import buttonRLForeground
import buttonRLHover
import buttonRLOverlay1
import buttonRLOverlay2
import com.badlogic.gdx.scenes.scene2d.Touchable
import org.roldy.core.utils.alpha
import org.roldy.core.utils.brighter
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.rendering.g2d.gui.*


@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.mainButton(
    text: TextManager,
    init: (@Scene2dDsl KTextButton).(S) -> Unit = {}
): KTextButton {
    val font = gui.font(60) {
        padTop = 0
        padBottom = 0
    }
    val padding = 30f
    lateinit var button: KTextButton
    table(true) { cell ->
        //background
        buttonRLBackground {
            image(this)
        }
        table(true) {
            pad(padding)
            buttonRLForeground {
                image(tint(gui.colors.primary))
            }
        }
        table(true) {
            pad(padding + 5)
            text { string ->
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
                    init(cell)
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