package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import org.roldy.data.item.ItemGrade
import org.roldy.gui.button.mainButton
import org.roldy.rendering.g2d.gui.*
import separatorHorizontal
import kotlin.properties.Delegates

private const val MaxCountDisplay = 99
private const val Columns = 8
private const val Rows = 8
private const val CellPadding = 5f
private const val GridWidth = Columns * SlotSize + Columns * (CellPadding * 2) + SlotSize + CellPadding * 2 + 55
private const val GridHeight = Rows * SlotSize - SlotSize / 2 - 2

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.inventory() =
    guiWindow(translate { inventory }) { contentCell ->
        contentCell.top().grow()
        lateinit var grid: KGrid
        val pool = pool<Actor, GuiContext>(100) {
            {
                inventorySlot {
                    onClick {

                    }
                }
            }
        }

        @Scene2dCallbackDsl
        fun addSlot() {
            with(pool) {
                grid.pull()
            }
        }
        table {
            scroll({
                it.width(GridWidth)
                    .height(GridHeight)
                    .align(Align.topLeft)
                    .fill()
            }) {
                grid(Columns, CellPadding) {
                    pad(15f)
                    align(Align.topLeft)
                    grid = this
                    repeat(49) {
                        addSlot()
                    }
                }
            }
        }
        row()
        buttons {
            mainButton(string { "Remove" }) {
                it.left().expand()
                onClick {
                    pool.push(grid.children.first())
                }
            }
            mainButton(string { "Add" }) {
                it.right().expand()
                onClick {
                    addSlot()

                }
            }
        }
    }

@Scene2dDsl
context(gui: GuiContext)
private fun KTable.buttons(build: (@Scene2dDsl KTable).() -> Unit = {}) {
    image(separatorHorizontal { this }) {
        it.fillX().pad(-30f).padTop(1f).padBottom(1f)
    }
    row()
    table { bottomCell ->
        bottomCell.fill().padRight(20f)
        row()
        build()
    }
}


@Scene2dCallbackDsl
data class InventorySlot(
    private val slot: Slot,
    private val setGrade: (String, Color) -> Unit,
    private val setCount: (String) -> Unit
) {
    var grade: ItemGrade? by Delegates.observable(null) { _, _, newValue ->
        newValue?.let {
            setGrade(newValue.name, newValue.color)
        } ?: setGrade("", Color.WHITE)
    }
    var count: Int? by Delegates.observable(null) { _, _, newValue ->
        newValue?.let {
            setCount(newValue.clampToString())
        } ?: setCount("")
    }

    private fun Int.clampToString() =
        when (this > MaxCountDisplay) {
            true -> "99+"
            false -> toString()
        }

    fun onClick(onClick: () -> Unit) {
        slot.onClick(onClick)
    }

    fun setIcon(drawable: Drawable?) {
        slot.setIcon(drawable)
    }

    fun clean() {
        setIcon(null)
        grade = null
        count = null
    }
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.inventorySlot(ref: (@Scene2dDsl InventorySlot).(KTable) -> Unit = {}) =
    slot { slotTable ->
        var gradeText = ""
        var countText = ""
        lateinit var label: KLabel
        val slot = InventorySlot(this, { name, color ->
            label.color = color
            gradeText = name
        }, {
            countText = it
        })

        content {
            align(Align.right)
            //grade
            label(string { gradeText }, params = {
                size = 30
                color = Color.WHITE
                borderWidth = 3f
                borderColor = Color.BLACK
                borderStraight = false
            }) {
                label = this
                it.expandY().top().right()
                autoupdate()
            }
            row()
            //count
            label(string { countText }, params = {
                size = 30
                borderWidth = 3f
                borderColor = Color.BLACK
                borderStraight = false
            }) {
                it.expandY().bottom().right()
                autoupdate()
            }
        }
        slot.ref(slotTable)
    }

