package org.roldy.gui.general

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.rendering.g2d.FontStyle
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.*

const val defaultFontSize = 35

val defaultLabelFontStyle = FontStyle.Default

class LabelActions(
    val label: UILabel,
    val gui: GuiContext
) {

    fun setText(text: String?) {
        if (text != null) {
            label.isVisible = true
            label.setText { text }
            label.updateText()
        } else {
            label.isVisible = false
        }
    }

    fun setText(text: TextManager) {
        context(gui) {
            label.setText(text.getText)
        }
    }

    fun setNumber(number: Number?) {
        setText(number?.toString())
    }

    @Scene2dCallbackDsl
    fun autoupdate() {
        label.autoupdate()
    }

}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.label(
    text: TextManager,
    fontSize: Int = defaultFontSize,
    style: FontStyle = defaultLabelFontStyle,
    params: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {},
    init: context(GuiContext) (@Scene2dDsl LabelActions).(S) -> Unit = {}
) =
    label(null, fontSize, style, params) {
        setText(text)
        init(it)
    }


@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.label(
    fontSize: Int = defaultFontSize,
    style: FontStyle = defaultLabelFontStyle,
    params: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {},
    init: context(GuiContext) (@Scene2dDsl LabelActions).(S) -> Unit = {}
) = label(null, fontSize, style, params, init)


@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.label(
    text: String?,
    fontSize: Int = defaultFontSize,
    style: FontStyle = defaultLabelFontStyle,
    params: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {},
    init: context(GuiContext) (@Scene2dDsl LabelActions).(S) -> Unit = {}
) =
    label(text, labelStyle {
        font = gui.font(style, fontSize, params)

        /* Markup example:
            [#FF0000]Health:[] 100
            [#00FF00]Energy:[] 50
         */
        font.data.markupEnabled = true
    }) {
        setText(text)
        LabelActions(this, gui).init(it)
    }