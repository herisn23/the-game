package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class KScrollPane(
    actor: Actor,
    style: ScrollPaneStyle
) : ScrollPane(actor, style) {
}


@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(_: C)
fun <A : Actor, S, C : KContext> KWidget<S>.scrollPane(
    init: context(C) (@Scene2dDsl KScrollPane).(S) -> Unit = {},
    style: ScrollPane.ScrollPaneStyle = scrollPaneStyle(),
    content: context(C) () -> A
): KScrollPane {
    contract { callsInPlace(content, InvocationKind.EXACTLY_ONCE) }
    return actor(KScrollPane(content(), style), init)
}

@Scene2dDsl
fun scrollPaneStyle(init: @Scene2dDsl ScrollPane.ScrollPaneStyle.() -> Unit = {}) =
    ScrollPane.ScrollPaneStyle().apply {
        init()
    }
