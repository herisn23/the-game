package org.roldy.core.ui.el

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import org.roldy.core.ui.Scene2dDsl
import org.roldy.core.ui.UIContext

class UIGroup: Group(), UIWidget<Unit> {
    override fun <T : Actor> storeActor(actor: T) {
        return addActor(actor)
    }
}


@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.group(
    build: context(C) (@Scene2dDsl UIGroup).(S) -> Unit
): UIGroup =
    actor(UIGroup(), build)
