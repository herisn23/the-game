package org.roldy.gui.widget

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.Align
import org.roldy.core.utils.sequencer
import org.roldy.data.state.InventoryItemState
import org.roldy.gui.GuiContext
import org.roldy.gui.general.WindowActions
import org.roldy.gui.general.label
import org.roldy.gui.general.tooltip.tooltip
import org.roldy.gui.string
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIWidget
import kotlin.properties.Delegates

class PlayerInventoryWindow(
    private val invActions: () -> WindowActions,
    val inventory: InventoryWindow<InventoryItemState>,
    val gui: GuiContext
) {
    val seq by sequencer(0, 1)
    val actions get() = invActions()
    var items: List<InventoryItemState> by Delegates.observable(emptyList()) { _, _, _ ->
        context(gui) {
            inventory.setData(data())
        }
    }

    init {

        inventory.onSlotPositionChanged { from, to ->
            val fromIndex = from.data!!.item.index

            // when slot has data, pick index from existing item else pick index from grid
            val targetIndex = to.data?.item?.index ?: to.gridIndex

            from.data!!.item.index = targetIndex
            to.data?.item?.index = fromIndex
        }

        context(gui) {
            inventory.onSlotClick { (slot, _) ->
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    lock = !lock
                    slot.data = toData()
                }
            }
            inventory.sort(string { "Grade" }) {
                sort(seq.next()) { item ->
                    item.grade
                }
                it.setData(data())

            }
            inventory.sort(string { "Count" }) {
                sort(seq.next()) { item ->
                    item.amount
                }
                it.setData(data())
            }
        }
    }

    context(gui: GuiContext)
    private fun InventoryItemState.toData() =
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

    context(gui: GuiContext)
    private fun data() =
        items.map { item ->
            item.toData()
        }

    fun <R : Comparable<R>> sort(dir: Int, selector: (InventoryItemState) -> R?) {
        // Get locked items with their indices
        val lockedByIndex = items.filter { it.lock }.associateBy { it.index }

        // Sort unlocked items
        val unlockedSorted = items.filterNot { it.lock }.sortedBy(selector).let {
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
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.playerInventory(init: (PlayerInventoryWindow) -> Unit) {
    lateinit var act: WindowActions
    val actions = inventory {

        init(PlayerInventoryWindow({ act }, this, gui))
    }
    act = actions
    actions.setPosition(gui.stage.width, 0f, Align.bottomRight)
}