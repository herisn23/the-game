package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Stack

@Scene2dDsl
class KStack : Stack(), KWidget<Unit> {
    override fun <T : Actor> storeActor(actor: T) {
        addActor(actor)
    }
}


@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.stack(
    build: context(C) (@Scene2dDsl KStack).(S) -> Unit
): KStack =
    actor(KStack(), build)

