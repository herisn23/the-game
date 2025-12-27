package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext

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
