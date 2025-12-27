package org.roldy.gui.general.button

import com.badlogic.gdx.scenes.scene2d.Touchable
import org.roldy.core.utils.alpha
import org.roldy.core.utils.brighter
import org.roldy.gui.*
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.anim.Normal
import org.roldy.rendering.g2d.gui.anim.Over
import org.roldy.rendering.g2d.gui.anim.Pressed
import org.roldy.rendering.g2d.gui.anim.transition
import org.roldy.rendering.g2d.gui.el.*
import org.roldy.rendering.g2d.gui.mask


@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.mainButton(
    text: TextManager,
    init: (@Scene2dDsl UITextButton).(S) -> Unit = {}
): UITextButton {
    val font = gui.font(60) {
        padTop = 0
        padBottom = 0
    }
    val padding = 30f
    lateinit var button: UITextButton
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
                        background = buttonRLHover {
                            mask(this)
                        },
                        transition = transition(
                            colors = mapOf(
                                Normal to (gui.colors.primary alpha 0f),
                                Pressed to (gui.colors.primary alpha .5f),
                                Over to (gui.colors.primary alpha 1f)
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