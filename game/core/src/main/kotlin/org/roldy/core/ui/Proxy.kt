package org.roldy.core.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.roldy.core.ui.el.UIWidget

@Scene2dDsl
class UITableProxy(val table: Table) : UIWidget<Cell<*>> {
    override fun <T : Actor> storeActor(actor: T): Cell<*> {
        return table.add(actor)
    }
}

@Scene2dDsl
context(_: C)
fun <S, C : UIContext> UIWidget<S>.proxy(
    table: Table,
    init: context(C) (@Scene2dDsl UITableProxy).() -> Unit = {}
): UITableProxy =
    UITableProxy(table).apply {
        init()
    }
