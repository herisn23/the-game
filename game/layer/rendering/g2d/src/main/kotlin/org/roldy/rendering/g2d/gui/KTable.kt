package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table

@Scene2dDsl
class KTable(
    val adAs: Table.(Actor) -> Unit = {
        add(it)
    },
) : Table(), KTableWidget

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
    build: context(C) (@Scene2dDsl KTable).(S) -> Unit
): KTable =
    actor(KTable(), build)