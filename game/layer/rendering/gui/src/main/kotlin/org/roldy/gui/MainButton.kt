package org.roldy.gui

import org.roldy.core.i18n.I18N
import org.roldy.core.utils.alpha
import org.roldy.core.utils.brighter
import org.roldy.core.utils.get
import org.roldy.core.utils.transparentColor
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.i18n.KLocalizableProxy
import org.roldy.rendering.g2d.gui.i18n.localizable
import org.roldy.rendering.g2d.gui.i18n.removable

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.mainButton(
    text: () -> I18N.Key,
    width: Float = 1f,
    height: Float = 1f,
    init: (@Scene2dDsl KTextButton).() -> Unit = {}
): KLocalizableProxy<KStack> {
    val width: Float = 735f * width
    val height: Float = 210f * height
    val font = gui.font(32) {
        padTop = 0
        padBottom = 0
    }
    val stretch = 30f
    val ninePatchParams = NinePatchParams(50, 50, 30, 30)
    lateinit var button: KLocalizableProxy<KStack>
    stack {
        setLayoutEnabled(false)

        //background
        ninePatch(gui.atlas["Button_RL_Background"], ninePatchParams) {
            setMinSize(width, height)
            image(this)
        }

        //foreground
        ninePatch(gui.atlas["Button_RL_Foreground"], ninePatchParams) {
            image(this) {
                stretch(
                    width,
                    height,
                    stretch = stretch
                )
                color = gui.colors.default
            }
        }

        //overlay 1
        ninePatch(gui.atlas["Button_RL_Overlay1"], 0, 0, 13, 0) {
            image(this) {
                stretch(
                    width,
                    76f,
                    bottom = 100f
                )
                color = transparentColor(0.5f)
            }
        }

        //overlay 2
        ninePatch(gui.atlas["Button_RL_Overlay2"], 30, 46, 27, 30) {
            image(this) {
                stretch(
                    width,
                    height,
                    left = stretch - 1,
                    right = stretch - 1,
                    top = stretch + 2,
                    bottom = stretch + 3
                )
                color = (gui.colors.default brighter 1.4f) alpha 0.75f
            }
        }

        ninePatch(gui.atlas["Button_RL_Hover"], left = 56, right = 56) {
            localizable(text) { string ->
                textButton(string, buttonStyle {
                    this.up = tint(transparentColor(0f))
                    this.down = tint(gui.colors.default alpha 0.25f)
                    this.over = tint(gui.colors.default alpha 0.15f)
                    this.font = font
                }) {
                    stretch(
                        width,
                        height,
                        stretch + 3
                    )
                    init()
                }
            }.also {
                //when button is removed we also remove a onLocaleChanged listener
                button = it.removable(this@stack)
            }
        }

    }
    return button
}