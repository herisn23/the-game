package org.roldy.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.rendering.g2d.gui.KScrollPane
import org.roldy.rendering.g2d.gui.KWidget
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.scrollPane

@Scene2dDsl
context(gui: GuiContext)
fun <S, A : Actor> KWidget<S>.scroll(
    init: context(GuiContext) (@Scene2dDsl KScrollPane).(S) -> Unit = {},
    content: context(GuiContext) () -> A
) =
    scrollPane({
        init(it)
        setFadeScrollBars(false)
        setScrollBarPositions(true, true)
        setScrollingDisabled(true, false)
    }, defaultScrollPaneStyle) {
        content()
    }