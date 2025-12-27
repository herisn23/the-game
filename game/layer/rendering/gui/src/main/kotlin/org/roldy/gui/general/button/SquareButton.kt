package org.roldy.gui.general.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.*

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.squareButton(
    icon: Drawable,
    init: (@Scene2dDsl UIImageButton).(S) -> Unit = {}
): UITable =
    table(true) { cell ->
        image(gui.region { Button_Square_Background })
        table(true) {
            pad(18f)
            image(gui.region { Button_Square_Foreground }) {
                color = gui.colors.primary
            }
            imageButton(
                drawable = mask(emptyImage(Color.WHITE)),
                transition = {
                    Normal to 0f
                    Pressed to .4f
                    Over to .7f
                }
            ) {
                image(icon)
                init(cell)
            }
        }
    }