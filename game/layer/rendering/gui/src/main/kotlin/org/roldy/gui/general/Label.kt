package org.roldy.gui.general

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UILabel
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.label
import org.roldy.rendering.g2d.gui.el.labelStyle

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.label(
    text: TextManager,
    fontSize: Int = 25,
    params: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {},
    init: context(GuiContext) (@Scene2dDsl UILabel).(S) -> Unit = {}
) =
    text { text ->
        label(text, labelStyle {
            font = gui.font(fontSize, params)

            /* Markup example:
                [#FF0000]Health:[] 100
                [#00FF00]Energy:[] 50
             */
            font.data.markupEnabled = true
        }) {
            init(it)
        }
    }

