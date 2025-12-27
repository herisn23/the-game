package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
interface UIWidget<out Storage> {
    fun <T : Actor> storeActor(actor: T): Storage
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
context(_: C)
inline fun <S, A : Actor, C : UIContext> UIWidget<S>.actor(
    actor: A,
    init: context(C) (@Scene2dDsl A).(S) -> Unit = {}
): A {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    actor.init(storeActor(actor))
    return actor
}