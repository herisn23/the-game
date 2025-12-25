package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.core.i18n.I18N
import org.roldy.core.utils.alpha
import org.roldy.core.utils.get
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.i18n.localizable

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.simpleButton(
    text: () -> I18N.Key,
    init: (@Scene2dDsl KTextButton).() -> Unit = {}
): Actor {
    val font = gui.font(25) {
        padTop = 0
        padBottom = 0
    }

    val padding = 20f
    return table(true) {
        //background
        ninePatch(gui.atlas["Button_RS_Background_Grayscale"], ninePatchParams(18)) {
            image(this) {
                color = gui.colors.default
            }
        }
        table(true) {
            pad(padding)
            //border
            ninePatch(gui.atlas["Button_RS_Border2"], ninePatchParams(16)) {
                image(this) {
//                    it.pad(20f)
                }
            }
            grow()
        }
        table(true) {
            pad(padding - 3) //pad to parent
            localizable(text) {
                textButton(
                    it, buttonStyle(
                        font = font,
                        background = buttonDrawable {
                            emptyImage(alpha(1f))
                        },
                        transition(
                            normalColor = Color.WHITE alpha 0f,
                            pressedColor = Color.WHITE alpha .1f,
                            overColor = Color.WHITE alpha 0.2f
                        )
                    )
                ) {
                    padLeft(padding)
                    padRight(padding)
                    addAction(updateTextAction())
                    init()
                }
            }
            grow()
        }
        grow()
    }
}