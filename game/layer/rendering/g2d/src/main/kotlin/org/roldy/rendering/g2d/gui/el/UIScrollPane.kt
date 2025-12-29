package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import org.roldy.core.eventEnter
import org.roldy.core.eventExit
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext
import org.roldy.rendering.g2d.gui.addInputListener
import org.roldy.rendering.g2d.gui.type
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
class UIScrollPane(
    actor: Actor,
    style: ScrollPaneStyle,
    stage: UIStage,
) : ScrollPane(actor, style) {

    init {
        addInputListener {
            type(eventExit) {
                stage.scrollFocus = null
            }
            type(eventEnter) {
                stage.scrollFocus = this@UIScrollPane
            }
        }
    }
}


@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(context: C)
fun <A : Actor, S, C : UIContext> UIWidget<S>.scrollPane(
    init: context(C) (@Scene2dDsl UIScrollPane).(S) -> Unit = {},
    style: ScrollPane.ScrollPaneStyle = scrollPaneStyle(),
    content: context(C) () -> A
): UIScrollPane {
    contract { callsInPlace(content, InvocationKind.EXACTLY_ONCE) }
    return actor(UIScrollPane(content(), style, context.stage()), init)
}

@Scene2dDsl
fun scrollPaneStyle(init: @Scene2dDsl ScrollPane.ScrollPaneStyle.() -> Unit = {}) =
    ScrollPane.ScrollPaneStyle().apply {
        init()
    }
