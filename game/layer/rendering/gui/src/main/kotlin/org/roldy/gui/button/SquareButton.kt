package org.roldy.gui.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.core.utils.get
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.squareButton(
    icon: Drawable,
    init: (@Scene2dDsl KImageButton).() -> Unit = {}
): KImageButton {
    lateinit var button: KImageButton
    table(true) {
        image(gui.atlas["Button_Square_Background"])

        table(true) {
            pad(18f)
            image(gui.atlas["Button_Square_Foreground"]) {
                color = gui.colors.default
            }
            imageButton(
                buttonDrawable(
                    drawable = mask(emptyImage(Color.WHITE)),
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