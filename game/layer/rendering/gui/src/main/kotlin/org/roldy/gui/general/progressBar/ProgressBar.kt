package org.roldy.gui.general.progressBar

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import org.roldy.gui.*
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIProgressBar
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.uiProgressBar
import org.roldy.rendering.g2d.gui.mask
import org.roldy.rendering.g2d.gui.redraw
import org.roldy.rendering.g2d.gui.traverse


enum class ProgressBarSize {
    Small, Medium
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.progressBar(
    size: ProgressBarSize,
    color: Color = gui.colors.progressBar,
    init: context(GuiContext) (@Scene2dDsl UIProgressBar).(S) -> Unit = {}
) = uiProgressBar(style = {
    when (size) {
        Small -> smallStyle(color)
        Medium -> mediumStyle(color)
    }
}) {
    init(it)
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