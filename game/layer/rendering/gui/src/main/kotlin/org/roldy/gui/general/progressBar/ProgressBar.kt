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

object ProgressBarAction : ImperativeAction

class ProgressBarDelegate : ImperativeActionDelegate<ProgressBarAction>() {
    object Amount : ImperativeValue<Float, ProgressBarAction>
    object AutoUpdate : ImperativeFunction<() -> Float, Unit, ProgressBarAction>
    object AutoUpdateRemove : ImperativeFunction<Unit, Unit, ProgressBarAction>

    val amount = Amount
    val autoUpdate = AutoUpdate
    val autoUpdateRemove = AutoUpdateRemove
    fun autoupdate(progress: () -> Float) {
        AutoUpdate(progress)
    }

    fun removeAutoupdate() {
        autoUpdateRemove(Unit)
    }
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
        amount init 0f
        amount(::setValue)

        function(ProgressBarDelegate.AutoUpdate) { progress ->
            clearActions()
            addAction(function {
                amount set progress()
            }.forever())
        }

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