package org.roldy.gui.general

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.gui.string
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UILabel
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.label
import org.roldy.rendering.g2d.gui.el.labelStyle

const val defaultFontSize = 25

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.label(
    text: TextManager,
    fontSize: Int = defaultFontSize,
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

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.label(
    text: String,
    fontSize: Int = defaultFontSize,
    params: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {},
    init: context(GuiContext) (@Scene2dDsl UILabel).(S) -> Unit = {}
) =
    this.label(string { text }, fontSize, params, init)


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
            setText(text.getText())
        }
    }

    fun setNumber(number: Number?) {
        setText(number?.toString())
    }

}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.label(
    params: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {},
    build: LabelActions.(@Scene2dDsl S) -> Unit,
) = label(defaultFontSize, params, build)

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.label(
    fontSize: Int = defaultFontSize,
    params: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {},
    build: LabelActions.(@Scene2dDsl S) -> Unit,
) =
    label("", fontSize, params) { storage ->
        val actions = LabelActions(this, gui)
        actions.build(storage)
    }
