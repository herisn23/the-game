package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Pool

class KPoolActor {

}

@Scene2dCallbackDsl
class KPool<A : Actor>(
    val getActor: KPool<A>.() -> KWidget<Unit>.() -> Unit,
    initialSize: Int,
) : Pool<A>(initialSize), KWidget<Unit> {
    lateinit var latestActor: A
    override fun newObject(): A {
        getActor()()
        return latestActor
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
        this.latestActor = actor as A
    }
}

@Scene2dCallbackDsl
context(_: C)
fun <A : Actor, C : KContext> pool(
    initialSize: Int = 16,
    newActor: KPool<A>.() -> KWidget<Unit>.() -> Unit
): KPool<A> =
    KPool(newActor, initialSize)