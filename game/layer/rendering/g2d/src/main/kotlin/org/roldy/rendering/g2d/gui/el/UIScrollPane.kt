package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class UIScrollPane(
    actor: Actor,
    style: ScrollPaneStyle
) : ScrollPane(actor, style) {
}


@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <A : Actor, S, C : UIContext> UIWidget<S>.scrollPane(
    init: context(C) (@Scene2dDsl UIScrollPane).(S) -> Unit = {},
    style: ScrollPane.ScrollPaneStyle = scrollPaneStyle(),
    content: context(C) () -> A
): UIScrollPane {
    contract { callsInPlace(content, InvocationKind.EXACTLY_ONCE) }
    return actor(UIScrollPane(content(), style), init)
}

@Scene2dDsl
fun scrollPaneStyle(init: @Scene2dDsl ScrollPane.ScrollPaneStyle.() -> Unit = {}) =
    ScrollPane.ScrollPaneStyle().apply {
        init()
    }
