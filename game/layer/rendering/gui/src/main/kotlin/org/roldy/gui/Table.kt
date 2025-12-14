package org.roldy.gui

import com.badlogic.gdx.scenes.scene2d.ui.Table


fun table(build: Table.(Table) -> Unit): Table {
    val table = Table()
    table.build(table)
    return table
}