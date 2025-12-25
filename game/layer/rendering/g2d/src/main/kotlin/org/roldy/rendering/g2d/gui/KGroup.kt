package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group

class KGroup: Group(), KWidget<Unit> {
    override fun <T : Actor> storeActor(actor: T) {
        return addActor(actor)
    }
}


@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.group(
    build: context(C) (@Scene2dDsl KGroup).(S) -> Unit
): KGroup =
    actor(KGroup(), build)
