package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import org.roldy.data.item.ItemGrade
import org.roldy.rendering.g2d.gui.*
import kotlin.properties.Delegates
import kotlin.random.Random

private const val MaxCountDisplay = 99
private const val Columns = 8
private const val Rows = 8
private const val CellPadding = 5f

context(gui: GuiContext)
val defaultScrollPaneStyle
    get() =
        scrollPaneStyle {

        }

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.inventory() =
    guiWindow(translate { inventory }) {
        val grades = ItemGrade.entries
        scrollPane({
            it.height(Rows * SlotSize)
            setFadeScrollBars(false)
            setScrollBarPositions(true, true)
            setScrollingDisabled(true, false)
        }, defaultScrollPaneStyle) {
            table {
                ScrollPane(this).apply {
                    this.setSize(4004f, 1000f)
                }
                defaults().pad(CellPadding)
                repeat(64) { i ->
                    inventorySlot {
                        grade = grades.random()
                        count = Random.nextInt(0, 10000)
                        onClick {
                            setIcon(gui.drawable { Icon_Sword_128 })
                        }
                    }
                    if ((i + 1) % Columns == 0) {
                        row()
                    }
                }
            }
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

    fun setIcon(drawable: Drawable) {
        slot.setIcon(drawable)
    }
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.inventorySlot(ref: (@Scene2dDsl InventorySlot).() -> Unit = {}) =
    slot {
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
        slot.ref()
    }

