package org.roldy.gui.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.*

enum class CircularButtonSize(
    val background: GuiContext.() -> TextureRegion,
    val foreground: GuiContext.() -> TextureRegion,
) {
    S(
        { region { Button_Circular_S_Background } },
        { region { Button_Circular_S_Foreground } }
    ),
    M(
        { region { Button_Circular_Background } },
        { region { Button_Circular_Foreground } }
    ),
    L(
        { region { Button_Circular_L_Background } },
        { region { Button_Circular_L_Foreground } }
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
        image(size.background(gui))

        table(true) {
            pad(18f)
            image(size.foreground(gui)) {
                color = gui.colors.primary
            }
            imageButton(
                buttonDrawable(
                    drawable = mask(gui.drawable { Button_Circular_Overlay }),
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