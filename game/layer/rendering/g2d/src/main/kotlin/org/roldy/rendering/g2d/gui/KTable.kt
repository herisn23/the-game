package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table

@Scene2dDsl
class KTable(
    val stackable: Boolean = false,
) : Table(), KTableWidget {
    private var stack: Cell<Stack>? = null
    fun rows(rows: Int) {
        repeat(rows) {
            row()
        }
    }

    override fun <T : Actor> storeActor(actor: T): Cell<T> {
        if (stackable) {
            if (stack == null) {
                stack = add(Stack())
                stack()
            }
            stack?.actor?.addActor(actor)
            return stack!! as Cell<T>
        } else {
            return super.storeActor(actor)
        }
    }

    fun grow() {
        stack?.grow()
    }
}

@Scene2dDsl
interface KTableWidget : KWidget<Cell<*>> {

    fun <T : Actor> add(actor: T): Cell<T>

    override fun <T : Actor> storeActor(actor: T): Cell<T> {
        val cell = add(actor)
        actor.userObject = cell
        return cell
    }
}


@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.table(
    stackable: Boolean = false,
    build: context(C) (@Scene2dDsl KTable).(S) -> Unit
): KTable =
    actor(KTable(stackable)) {
        build(it)
        if(stackable) {
            grow()
        }
    }


