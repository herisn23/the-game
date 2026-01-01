package org.roldy.gui.general.button

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.anim.alphaAnimation
import org.roldy.rendering.g2d.gui.anim.colorAnimation
import org.roldy.rendering.g2d.gui.anim.noneAnimation
import org.roldy.rendering.g2d.gui.anim.stackedAnimation
import org.roldy.rendering.g2d.gui.el.*
import org.roldy.rendering.g2d.pixmap

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.squareButton(
    icon: Drawable,
    init: (@Scene2dDsl UIPlainButton).(S) -> Unit = {}
): UITable =
    table { storage ->
        val padding = 18f
        pad(padding)
        plainButton { cell ->
            pad(padding)
            val background = noneAnimation(gui.drawable { Button_Square_Background }.pad(-padding))
            val foreground = colorAnimation(gui.drawable { Button_Square_Foreground }.apply {
                cell.width(minWidth).height(minHeight)
            }) {
                color = gui.colors.primary
                Disabled have gui.colors.disabled
            }
            val hover = alphaAnimation(mask(pixmap())) {
                Normal have 0f
                Pressed have .1f
                Over have .7f
                Disabled have 0f
            }
            graphics {
                stackedAnimation {
                    add(background)
                    add(foreground)
                    add(hover)
                }
            }
            image(icon)
            init(storage)
        }
    }