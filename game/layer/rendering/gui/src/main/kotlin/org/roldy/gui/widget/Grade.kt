package org.roldy.gui.widget

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import org.roldy.data.item.ItemGrade
import org.roldy.gui.GuiContext
import org.roldy.gui.general.LabelActions
import org.roldy.gui.general.defaultFontSize
import org.roldy.gui.general.defaultLabelFontStyle
import org.roldy.gui.general.label
import org.roldy.rendering.g2d.FontStyle
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIWidget


class GradeAction(
    val labelActions: LabelActions
) {
    val label get() = labelActions.label

    fun setGrade(itemGrade: ItemGrade?) {
        labelActions.setText(itemGrade?.toString())
        label.color = itemGrade?.color ?: Color.WHITE
    }

}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.gradeLabel(
    fontStyle: FontStyle = defaultLabelFontStyle,
    params: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {},
    build: GradeAction.(@Scene2dDsl S) -> Unit,
) = gradeLabel(defaultFontSize, fontStyle, params, build)

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.gradeLabel(
    fontSize: Int,
    fontStyle: FontStyle = defaultLabelFontStyle,
    params: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {},
    build: GradeAction.(@Scene2dDsl S) -> Unit,
) =
    label(fontSize, fontStyle, params) {
        val action = GradeAction(this)
        action.build(it)
    }