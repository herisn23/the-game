package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table

@Scene2dDsl
class KTableProxy(val table: Table) : KWidget<Cell<*>> {
    override fun <T : Actor> storeActor(actor: T): Cell<*> {
        return table.add(actor)
    }
}

@Scene2dDsl
context(_: C)
fun <S, C : KContext> KWidget<S>.proxy(
    table: Table,
    init: context(C) (@Scene2dDsl KTableProxy).() -> Unit = {}
): KTableProxy =
    KTableProxy(table).apply {
        init()
    }
