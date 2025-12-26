package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Pool

@Scene2dCallbackDsl
class KPool<A : Actor>(
    val getActor: KPool<A>.() -> KWidget<Unit>.() -> A,
    initialSize: Int,
) : Pool<A>(initialSize), KWidget<Unit> {
    override fun newObject(): A {
        return getActor()()
    }

    init {
        repeat(initialSize) {
            free(newObject())
        }
    }

    context(_: C)
    fun <S, C : KContext> KWidget<S>.pull(): A =
        actor(this@KPool.obtain())

    @Scene2dCallbackDsl
    fun push(actor: A): Unit =
        this@KPool.free(actor).also {
            actor.remove()
        }

    override fun <T : Actor> storeActor(actor: T) {
        //we can keep it empty
    }
}

@Scene2dCallbackDsl
context(_: C)
fun <A : Actor, C : KContext> pool(
    initialSize: Int = 16,
    newActor: KPool<A>.() -> KWidget<Unit>.() -> A
): KPool<A> =
    KPool(newActor, initialSize)