package org.roldy.gui.general.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.anim.Normal
import org.roldy.rendering.g2d.gui.anim.Over
import org.roldy.rendering.g2d.gui.anim.Pressed
import org.roldy.rendering.g2d.gui.anim.transition
import org.roldy.rendering.g2d.gui.el.*
import org.roldy.rendering.g2d.gui.mask

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.squareButton(
    icon: Drawable,
    init: (@Scene2dDsl UIImageButton).(S) -> Unit = {}
): org.roldy.rendering.g2d.gui.el.UITable =
    table(true) { cell ->
        image(gui.region { Button_Square_Background })
        table(true) {
            pad(18f)
            image(gui.region { Button_Square_Foreground }) {
                color = gui.colors.primary
            }
            imageButton(
                drawable = mask(emptyImage(Color.WHITE)),
                transition = transition(
                    colors = mapOf(
                        Normal to (Color.WHITE alpha 0f),
                        Pressed to (Color.WHITE alpha .4f),
                        Over to (Color.WHITE alpha .7f)
                    )
                )
            ) {
                image(icon)
                init(cell)
            }
        }
    }