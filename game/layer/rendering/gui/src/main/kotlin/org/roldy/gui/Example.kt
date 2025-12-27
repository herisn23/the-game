package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import org.roldy.core.utils.sequencer
import org.roldy.data.item.ItemGrade
import org.roldy.gui.general.button.mainButton
import org.roldy.gui.general.button.smallButton
import org.roldy.gui.general.button.squareButton
import org.roldy.gui.general.label
import org.roldy.gui.widget.Inventory
import org.roldy.gui.widget.data
import org.roldy.gui.widget.inventory
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.onClick
import org.roldy.rendering.g2d.gui.el.table
import kotlin.contracts.ExperimentalContracts
import kotlin.random.Random
import kotlin.system.exitProcess

@OptIn(ExperimentalContracts::class)
@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.example() {
//    image(emptyImage(Color.BLACK brighter 1.5f)) {
//        setSize(stage.width, stage.height)
//    }

    data class InventoryItem(
        var index: Int,
        val grade: ItemGrade,
        val count: Int
    )

    val playerInventory = (0..3).mapIndexed { index, item ->
        InventoryItem(
            index,
            ItemGrade.entries.random(),
            Random.nextInt(0, 200)

        )
    }.toMutableList()

    fun data() =
        playerInventory.map { item ->
            data(
                gui.drawable { Icon_Sword_128 },
                item.grade,
                item.count,
                item.index,
                item
            ) { data ->
                pad(200f)
                label(string { data.grade?.name ?: "" }, 60) {
                    color = data.grade?.color ?: Color.WHITE
                }
            }
        }

    val seq by sequencer(1, 2)
    lateinit var inv: Inventory<InventoryItem>
    val window = inventory {

        setData(data(), false)
        inv = this
        maxSlots = 10

        onSlotPositionChanged { from, to ->
            // from is always not null by design
            val fromIndex = from.slotData!!.index

            // when slot has data, pick index from existing item else pick index from grid
            val targetIndex = to.slotData?.data?.index ?: to.gridIndex

            from.slotData!!.data.index = targetIndex
            to.slotData?.data?.index = fromIndex
        }

        onSlotClick {
            println("click")
        }
        sort(string { "Sort 1" }) {
            playerInventory.sortBy {
                if (seq.current == 1) {
                    it.grade.ordinal
                } else {
                    -it.grade.ordinal
                }
            }
            it.setData(data())
            seq.next()
        }
        sort(string { "Sort 2" }) {
            playerInventory.sortBy {
                if (seq.current == 2) {
                    it.count
                } else {
                    -it.count
                }
            }
            it.setData(data())
            seq.next()
        }
        sort(string { "Reset" }) {
            playerInventory.sortBy {
                if (seq.current == 2) {
                    it.count
                } else {
                    -it.count
                }
            }
            it.setData(data(), false)
            seq.next()
        }
    }

    table {
        align(Align.top)
        setFillParent(true)

        mainButton(string { "Open window" }) {
            onClick {
                window.open()
            }
        }
        squareButton(gui.drawable { Ico_Plus }) {
            onClick {
                inv.maxSlots += 1
            }
        }
//        circularButton(gui.drawable { Ico_Plus })
//        circularButton(gui.drawable { Ico_Plus }, CircularButtonSize.S)
//        circularButton(gui.drawable { Ico_Plus }, CircularButtonSize.L)
        smallButton(translate { close }) {
            onClick {
                exitProcess(1)
            }
        }
        row()

    }
}