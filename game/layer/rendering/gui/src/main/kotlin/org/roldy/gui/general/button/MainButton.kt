package org.roldy.gui.general.button

import org.roldy.core.utils.alpha
import org.roldy.gui.*
import org.roldy.rendering.g2d.FontStyle
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.anim.alphaAnimation
import org.roldy.rendering.g2d.gui.anim.colorAnimation
import org.roldy.rendering.g2d.gui.anim.noneAnimation
import org.roldy.rendering.g2d.gui.anim.stackedAnimation
import org.roldy.rendering.g2d.gui.el.UITextButton
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.table
import org.roldy.rendering.g2d.gui.el.textButton


@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.mainButton(
    text: TextManager,
    init: (@Scene2dDsl UITextButton).(S) -> Unit = {}
): UITextButton {
    val font = gui.font(FontStyle.Default, 60) {
        padTop = 0
        padBottom = 0
    }
    val padding = 30f
    lateinit var button: UITextButton
    table { storage ->
        pad(padding / 2)
        text { string ->
            textButton(
                { string().uppercase() }, font
            ) { cell ->
                pad(padding)
                val background = noneAnimation(buttonRLBackground {
                    pad(-padding)
                })
                val foreground = colorAnimation(buttonRLForeground {
                    apply {
                        cell.minWidth(minWidth).minHeight(minHeight)
                    }
                }) {
                    color = gui.colors.primary
                    Disabled have gui.colors.disabled
                }
                val overlay = noneAnimation(buttonRLOverlay1 {
                    mask(tint(alpha(.7f))) redraw { x, y, width, height, draw ->
                        draw(x, y + (height) / 2, width, 76f)
                    }
                })
                val decor = noneAnimation(buttonRLOverlay2 {
                    mask(tint(alpha(.4f)))
                        .pad(5f, -1f, 5f, -1f)
                })
                val hover = alphaAnimation(buttonRLHover {
                    mask(pad(2f))
                }) {
                    Normal have 0f
                    Pressed have .1f
                    Over have .6f
                    Disabled have 0f
                }

                graphics {
                    stackedAnimation {
                        add(background)
                        add(foreground)
                        add(overlay)
                        add(decor)
                        add(hover)
                    }
                }

                init(storage)
                button = this
            }
        }
    }
    return button
}