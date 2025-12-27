package org.roldy.gui.general.button

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.*

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
fun <S> UIWidget<S>.circularButton(
    icon: Drawable,
    size: CircularButtonSize = CircularButtonSize.M,
    init: (@Scene2dDsl UIImageButton).() -> Unit = {}
): UIImageButton {
    lateinit var button: UIImageButton
    table(true) {
        image(size.background(gui))

        table(true) {
            pad(18f)
            image(size.foreground(gui)) {
                color = gui.colors.primary
            }
            imageButton(
                drawable = mask(gui.drawable { Button_Circular_Overlay }),
                transition = {
                    Normal to 0f
                    Pressed to .4f
                    Over to 1f
                }
            ) {
                image(icon)
                button = this
                init()
            }
        }
    }
    return button
}