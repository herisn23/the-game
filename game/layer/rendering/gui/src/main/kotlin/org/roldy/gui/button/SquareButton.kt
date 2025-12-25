package org.roldy.gui.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.squareButton(
    icon: Drawable,
    init: (@Scene2dDsl KImageButton).(S) -> Unit = {}
): KTable {
    return table(true) { cell ->
        image(gui.region { Button_Square_Background })
        table(true) {
            pad(18f)
            image(gui.region { Button_Square_Foreground }) {
                color = gui.colors.primary
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
                init(cell)
            }
        }
    }
}