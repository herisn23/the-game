package org.roldy.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import org.roldy.core.i18n.format.Genus
import org.roldy.core.utils.hex
import org.roldy.core.utils.sequencer
import org.roldy.data.item.ItemGrade
import org.roldy.gui.general.button.*
import org.roldy.gui.general.label
import org.roldy.gui.general.tooltip.tooltip
import org.roldy.gui.widget.Inventory
import org.roldy.gui.widget.data
import org.roldy.gui.widget.gradeLabel
import org.roldy.gui.widget.inventory
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.image
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

    val list = (0..60).map { index ->
        InventoryItem(
            index,
            if (index == 2) null else ItemGrade.entries.random(),
            Random.nextInt(0, 20),
            index == 1

        )
    }.toMutableList()

    fun InventoryItem.toData() =
        data(
            gui.drawable { Icon_Sword_128 },
            grade,
            amount,
            lock,
            index,
            this
        ) { data ->
            gradeLabel {
                setGrade(data.grade)
                label.tooltip(this@data.root) {
                    content {
                        label("Grade affects 1234")
                    }
                }
            }
        }

    fun data() =
        list.map { item ->
            item.toData()
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

    image(emptyImage(Color.BLACK)) {
        setFillParent(true)
    }

    val seq by sequencer(0, 1)
    lateinit var inv: Inventory<InventoryItem>
    val window = inventory {
        inv = this
        setData(data())
        maxSlots = 10

        onSlotPositionChanged { from, to ->
            val fromIndex = from.data!!.item.index

            // when slot has data, pick index from existing item else pick index from grid
            val targetIndex = to.data?.item?.index ?: to.gridIndex

            from.data!!.item.index = targetIndex
            to.data?.item?.index = fromIndex
        }

        onSlotClick { (slot, _) ->
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                lock = !lock
                slot.data = toData()
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
    window.setPosition(gui.stage.width - window.width, 0f)

    table {
        this.pad(20f)
        name = "ExampleTable"
        align(Align.top)
        setFillParent(true)


        mainButton(string { "Close window and do something more" }) {
            name = "Close window"
//            isDisabled = true
            onClick {
                isDisabled = true
                window.close()
            }
        }
        mainButton(string { "ěščřžýáíé" }) {
            onClick {
                window.open()
            }
        }
        squareButton(gui.drawable { Ico_Plus }) {
            onClick {
                inv.maxSlots += 1
            }
        }
        squareButton(gui.drawable { Ico_Menu }) {
            isDisabled = true
        }
        circularButton(gui.drawable { Ico_Plus }, CircularButtonSize.S)
        circularButton(gui.drawable { Ico_Plus })
        circularButton(gui.drawable { Ico_Back }, CircularButtonSize.L) {
            isDisabled = true
        }
        smallButton(translate { close }) {
            onClick {
                exitProcess(1)
            }
        }
        smallButton(string { "a" }, gui.colors.secondary) {
            isDisabled = true
        }
        row()
        label(translate { compositeText
            .arg("pulse", 120)
            .arg("color", gui.colors.secondary.hex)
            .arg("hodnota", "hodnota")
            .genus(Genus.Masculine)
        })
        label(translate { mine_supplies.arg("supplies", 1) })
        row()
        gui.i18n.selections({selectiveText}).forEach {
            label(string { it })
            row()
        }
    }
}