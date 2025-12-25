package org.roldy.gui.button

import com.badlogic.gdx.scenes.scene2d.Touchable
import org.roldy.core.i18n.I18N
import org.roldy.core.utils.alpha
import org.roldy.core.utils.brighter
import org.roldy.core.utils.get
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
    val ninePatchParams = ninePatchParams(left = 50, right = 50, top = 30, bottom = 30)
    lateinit var button: KTextButton
    table(true) {
        //background
        ninePatch(gui.atlas["Button_RL_Background"], ninePatchParams) {
            image(this)
        }

        table(true) {
            pad(padding)
            ninePatch(gui.atlas["Button_RL_Foreground"], ninePatchParams) {
                image(tint(gui.colors.default))
            }
            localizable(text) { string ->
                textButton(
                    string, textButtonStyle(
                        font = font,
                        background = buttonDrawable(
                            drawable = ninePatch(gui.atlas["Button_RL_Hover"], left = 56, right = 56) {
                                mask(this)
                            },
                            transition = transition(
                                normalColor = gui.colors.default alpha 0f,
                                pressedColor = gui.colors.default alpha .5f,
                                overColor = gui.colors.default alpha 1f
                            )
                        )
                    )
                ) {
                    addAction(updateTextAction())
                    init()
                    button = this
                }
            }
            ninePatch(gui.atlas["Button_RL_Overlay1"], 0, 0, 13, 0) {
                image(tint(alpha(.7f))) {
                    touchable = Touchable.disabled
                }
            }

            table(true) {
                pad(1f, -1f, 5f, -1f)
                //overlay 2
                ninePatch(gui.atlas["Button_RL_Overlay2"], 30, 46, 27, 30) {
                    image(tint((gui.colors.default brighter 2f) alpha 0.25f)) {
                        touchable = Touchable.disabled
                    }
                }
            }
        }
    }
    return button
}