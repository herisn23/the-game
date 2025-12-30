package org.roldy.gui.general.button

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.anim.alphaAnimation
import org.roldy.rendering.g2d.gui.anim.colorAnimation
import org.roldy.rendering.g2d.gui.anim.noneAnimation
import org.roldy.rendering.g2d.gui.anim.stackedAnimation
import org.roldy.rendering.g2d.gui.el.*

enum class CircularButtonSize(
    val background: GuiContext.() -> Drawable,
    val foreground: GuiContext.() -> Drawable,
) {
    S(
        { drawable { Button_Circular_S_Background } },
        { drawable { Button_Circular_S_Foreground } }
    ),
    M(
        { drawable { Button_Circular_Background } },
        { drawable { Button_Circular_Foreground } }
    ),
    L(
        { drawable { Button_Circular_L_Background } },
        { drawable { Button_Circular_L_Foreground } }
    ),
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.circularButton(
    icon: Drawable,
    size: CircularButtonSize = CircularButtonSize.M,
    init: (@Scene2dDsl UIPlainButton).(S) -> Unit = {}
): UIPlainButton {
    lateinit var button: UIPlainButton
    val padding = 18f
    table { storage ->
        pad(padding)
        plainButton { cell ->
            val background = noneAnimation(size.background(gui).pad(-padding))
            val foreground = colorAnimation(size.foreground(gui).apply {
                cell.minWidth(minWidth).minHeight(minHeight)
            }) {
                color = gui.colors.primary
                Disabled have gui.colors.disabled
            }
            val hover = alphaAnimation(mask(gui.drawable { Button_Circular_Overlay })) {
                Normal have 0f
                Pressed have .4f
                Over have 1f
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
            button = this
            init(storage)
        }
    }
    return button
}