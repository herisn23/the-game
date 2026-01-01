package org.roldy.gui.general.progressBar

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import org.roldy.gui.*
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.uiProgressBar


enum class ProgressBarSize {
    Small, Medium
}

class ProgressBarDelegate : ImperativeActionDelegate() {
    object Amount : ImperativeValue<Float>
    val amount = Amount
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.progressBar(
    size: ProgressBarSize,
    color: Color = gui.colors.progressBar,
    init: context(GuiContext) (@Scene2dDsl ProgressBarDelegate).(S) -> Unit = {}
) = delegate(ProgressBarDelegate()) {
    uiProgressBar(style = {
        when (size) {
            Small -> smallStyle(color)
            Medium -> mediumStyle(color)
        }
    }) {
        amount(::setValue)
        amount set 0f
        init(it)
    }
}

context(gui: GuiContext)
private fun ProgressBar.ProgressBarStyle.smallStyle(color: Color) {
    background = progressBarStandardBackgroundS { this }
    knobBefore = progressBarStandardFillS { tint(color) }
}

context(gui: GuiContext)
private fun ProgressBar.ProgressBarStyle.mediumStyle(color: Color) {
    background = progressBarStandardBackgroundM { this }
    knobBefore = progressBarStandardFillM {
        tint(color)
    } traverse mask(gui.drawable {
        ProgressBar_Standard_Overlay_M
    }.run {
        val overlayWidth = minWidth
        redraw { x, y, w, h, draw ->
            val width = overlayWidth.takeIf { overlayWidth <= w } ?: w
            draw(x + w - width, y, width, h)
        }
    })
}