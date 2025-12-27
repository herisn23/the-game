package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext

@Scene2dDsl
class UIStack : Stack(), UIWidget<Unit> {
    override fun <T : Actor> storeActor(actor: T) {
        addActor(actor)
    }
}


@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.stack(
    build: context(C) (@Scene2dDsl UIStack).(S) -> Unit
): UIStack =
    actor(UIStack(), build)

