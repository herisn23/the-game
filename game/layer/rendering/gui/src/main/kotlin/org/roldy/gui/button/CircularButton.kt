package org.roldy.gui.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.core.utils.drawable
import org.roldy.core.utils.get
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.*

enum class CircularButtonSize(
    val background: String,
    val foreground: String,
) {
    S(
        "Button_Circular_S_Background",
        "Button_Circular_S_Foreground"
    ),
    M(
        "Button_Circular_Background",
        "Button_Circular_Foreground"
    ),
    L(
        "Button_Circular_L_Background",
        "Button_Circular_L_Foreground"
    ),
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.circularButton(
    icon: Drawable,
    size: CircularButtonSize = CircularButtonSize.M,
    init: (@Scene2dDsl KImageButton).() -> Unit = {}
): KImageButton {
    lateinit var button: KImageButton
    table(true) {
        image(gui.atlas[size.background])

        table(true) {
            pad(18f)
            image(gui.atlas[size.foreground]) {
                color = gui.colors.default
            }
            imageButton(
                buttonDrawable(
                    drawable =  mask(gui.atlas.drawable("Button_Circular_Overlay")),
                    transition = transition(
                        normalColor = Color.WHITE alpha 0f,
                        pressedColor = Color.WHITE alpha .4f,
                        overColor = Color.WHITE alpha .7f
                    )
                )
            ) {
                image(icon)
                button = this
                init()
            }
        }
    }
    return button
}