package org.roldy.core.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Pool
import org.roldy.core.ui.el.UIWidget
import org.roldy.core.ui.el.actor

interface PoolItem {
    fun clean()
}

@Scene2dCallbackDsl
class UIPool<A : Actor>(
    val getActor: UIPool<A>.() -> UIWidget<Unit>.() -> A,
    initialSize: Int,
) : Pool<A>(initialSize), UIWidget<Unit> {
    override fun newObject(): A {
        return getActor()()
    }

    init {
        repeat(initialSize) {
            free(newObject())
        }
    }

    context(_: C)
    fun <S, C : UIContext> UIWidget<S>.pull(): A =
        actor(this@UIPool.obtain())

    @Scene2dCallbackDsl
    fun push(actor: A): Unit =
        this@UIPool.free(actor).also {
            actor.remove()

            if (actor.userObject is PoolItem) {
                (actor.userObject as PoolItem).clean()
            }
        }

    override fun <T : Actor> storeActor(actor: T) {
        //we can keep it empty
    }
}

@Scene2dCallbackDsl
context(_: C)
fun <A : Actor, C : UIContext> pool(
    initialSize: Int = 16,
    newActor: UIPool<A>.() -> UIWidget<Unit>.() -> A
): UIPool<A> =
    UIPool(newActor, initialSize)