package org.roldy.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import org.roldy.core.utils.sequencer
import org.roldy.data.item.ItemGrade
import org.roldy.gui.general.button.mainButton
import org.roldy.gui.general.button.smallButton
import org.roldy.gui.general.button.squareButton
import org.roldy.gui.general.label
import org.roldy.gui.general.tooltip.tooltip
import org.roldy.gui.widget.Inventory
import org.roldy.gui.widget.data
import org.roldy.gui.widget.gradeLabel
import org.roldy.gui.widget.inventory
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.onClick
import org.roldy.rendering.g2d.gui.el.table
import kotlin.random.Random
import kotlin.system.exitProcess

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.example() {
    data class InventoryItem(
        var index: Int,
        var grade: ItemGrade?,
        var amount: Int,
        var lock: Boolean
    )

    val list = (0..6).map { index ->
        InventoryItem(
            index,
            if (index == 2) null else ItemGrade.entries.random(),
            Random.nextInt(0, 20),
            index == 1

        )
    }.toMutableList()

    fun data() =
        list.map { item ->
            data(
                gui.drawable { Icon_Sword_128 },
                item.grade,
                item.amount,
                item.lock,
                item.index,
                item
            ) { data ->
                pad(200f)
                gradeLabel(60) {
                    touchable = Touchable.enabled
                    setGrade(data.grade)
                    tooltip(label) {
                        name = "Second"
                        content {
                            pad(300f)
                            label(string { "second tooltip" }) {
                                this@content.tooltip(this) {
                                    name = "Third"
                                    content {
                                        pad(400f)
                                        label("third tooltip") {
                                            this@content.tooltip(this) {
                                                name = "Fourth"
                                                content {
                                                    pad(500f)
                                                    label("fourth tooltip") {

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    fun <R : Comparable<R>> sort(dir: Int, selector: (InventoryItem) -> R?) {
        // Get locked items with their indices
        val lockedByIndex = list.filter { it.lock }.associateBy { it.index }

        // Sort unlocked items
        val unlockedSorted = list.filterNot { it.lock }.sortedBy(selector).let {
            if (dir == 1) {
                it.reversed()
            } else {
                it
            }
        }
        var nextIndex = 0
        // Generate available indices starting from 1, skipping locked indices
        unlockedSorted.forEachIndexed { i, item ->
            val lockedIndex = lockedByIndex[i]
            if (lockedIndex != null) {
                nextIndex++
            }
            item.index = i + nextIndex
        }
    }

    val seq by sequencer(0, 1)
    lateinit var inv: Inventory<InventoryItem>
    val window = inventory {

        setData(data())
        inv = this
        maxSlots = 10

        onSlotPositionChanged { from, to ->
            val fromIndex = from.slotData!!.data.index

            // when slot has data, pick index from existing item else pick index from grid
            val targetIndex = to.slotData?.data?.index ?: to.gridIndex

            from.slotData!!.data.index = targetIndex
            to.slotData?.data?.index = fromIndex
        }

        onSlotClick {
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                lock = !lock
                setData(data())
            }
        }
        sort(string { "Grade" }) {
            sort(seq.next()) { item ->
                item.grade
            }
            it.setData(data())

        }
        sort(string { "Count" }) {
            sort(seq.next()) { item ->
                item.amount
            }
            it.setData(data())
        }
    }

    table {
        name = "ExampleTable"
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