package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext

class UIProgressBar(
    min: Float,
    max: Float,
    stepSize: Float,
    vertical: Boolean = false,
    style: ProgressBarStyle
) : ProgressBar(min, max, stepSize, vertical, style)


@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.uiProgressBar(
    min: Float = 0f,
    max: Float = 1f,
    stepSize: Float = .001f,
    vertical: Boolean = false,
    style: ProgressBarStyle.() -> Unit = {},
    init: context(C) (@Scene2dDsl UIProgressBar).(S) -> Unit = {}
): UIProgressBar {
    return actor(UIProgressBar(min, max, stepSize, vertical, ProgressBarStyle().apply {
        style()
    }), init)
}