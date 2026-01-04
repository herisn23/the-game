package org.roldy.gui.general

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIScrollPane
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.scrollPane
import org.roldy.rendering.g2d.gui.el.scrollPaneStyle

private const val scrollSize = 18

context(gui: GuiContext)
val defaultScrollPaneStyle
    get() =
        scrollPaneStyle {
            hScrollKnob = gui.pixmap(Color.BLACK alpha .4f, height = scrollSize)
            hScroll = gui.pixmap(gui.colors.secondaryText alpha .2f, height = scrollSize)

            vScrollKnob = gui.pixmap(Color.BLACK alpha .4f, width = scrollSize)
            vScroll = gui.pixmap(gui.colors.secondaryText alpha .2f, width = scrollSize)
        }


@Scene2dDsl
context(gui: GuiContext)
fun <S, A : Actor> UIWidget<S>.scroll(
    init: context(GuiContext) (@Scene2dDsl UIScrollPane).(S) -> Unit = {},
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