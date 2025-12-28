package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.roldy.core.cast
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.UIContext

@Scene2dDsl
class UITable(
    val stackable: Boolean = false,
) : Table(), UITableWidget {
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
            }
            stack?.actor?.addActor(actor)
            stack()
            return stack!! as Cell<T>
        } else {
            return super.storeActor(actor)
        }
    }

    fun grow() {
        stack?.grow()
    }

    fun rebuild() {
        if (stackable) {
            grow()
        } else {
            pack()
        }
    }
}

@Scene2dDsl
interface UITableWidget : UIWidget<Cell<*>> {

    fun <T : Actor> add(actor: T): Cell<T>

    override fun <T : Actor> storeActor(actor: T): Cell<T> =
        add(actor)
}


@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.table(
    stackable: Boolean = false,
    build: context(C) (@Scene2dDsl UITable).(S) -> Unit
): UITable =
    actor(UITable(stackable)) {
        build(it)
        rebuild()
    }


fun <D> Actor.setData(data: D) {
    userObject = data
}

inline fun <reified D> Actor.getData() =
    userObject.cast<D>()

