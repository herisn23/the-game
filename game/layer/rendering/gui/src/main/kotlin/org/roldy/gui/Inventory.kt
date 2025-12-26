package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import org.roldy.core.cast
import org.roldy.data.item.ItemGrade
import org.roldy.gui.button.mainButton
import org.roldy.gui.popup.popup
import org.roldy.rendering.g2d.gui.*
import kotlin.properties.Delegates

private const val MaxCountDisplay = 99
private const val Columns = 8
private const val Rows = 8
private const val CellPadding = 5f
private const val GridWidth = Columns * SlotSize + Columns * (CellPadding * 2) + SlotSize + CellPadding * 2 + 55
private const val GridHeight = Rows * SlotSize - SlotSize / 2
private const val MinCells = 48

@Scene2dCallbackDsl
data class InventorySlot<T>(
    private val inventory: Inventory<T>,
    private val table: KTable,
    private val slot: Slot,
    private val onRemoved: () -> Unit,
    private val setGrade: (ItemGrade?) -> Unit,
    private val setCount: (String) -> Unit
) : PoolItem {
    var slotData: Data<T>? = null
    internal lateinit var tooltip: KContextualPopup

    data class Data<A>(
        val icon: Drawable,
        val grade: ItemGrade? = null,
        val count: Int? = null,
        val item: A,
        val tooltip: (@Scene2dCallbackDsl KTable).(Data<A>) -> Unit
    )

    val occupied get() = slotData != null

    var isDisabled: Boolean
        get() = slot.isDisabled
        set(value) {
            slot.isDisabled = value
        }

    var grade: ItemGrade? by Delegates.observable(null) { _, _, newValue ->
        newValue?.let {
            setGrade(newValue)
        } ?: setGrade(null)
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

    fun onClick(onClick: InventorySlot<T>.() -> Unit) {
        slot.onClick {
            this.onClick()
        }
    }

    fun setData(data: Data<T>) {
        this.slotData = data
        this.count = data.count
        this.grade = data.grade
        tooltip {
            with(data) {
                tooltip(data)
            }
        }
        setIcon(data.icon)
    }

    fun setIcon(drawable: Drawable?) {
        slot.setIcon(drawable)
    }


    fun tooltip(content: KTable.() -> Unit) {
        this.tooltip.content {
            content()
        }
    }

    override fun clean() {
        setIcon(null)
        grade = null
        count = null
        isDisabled = false
        slotData = null
        tooltip.clean()
    }

    fun remove() {
        table.remove()
        onRemoved()
    }
}

fun <T> data(
    icon: Drawable,
    grade: ItemGrade? = null,
    count: Int? = null,
    item: T? = null,
    tooltip: (@Scene2dCallbackDsl KTable).(InventorySlot.Data<T?>) -> Unit = {}
) = InventorySlot.Data(icon, grade, count, item, tooltip)


class Inventory<T>(
    private val grid: KGrid,
    private val context: GuiContext
) {
    var maxSlots: Int by Delegates.observable(0) { _, _, _ ->
        updateSlots()
    }

    val slots: List<InventorySlot<T>>
        get() = grid.children.map {
            it.userObject.cast()
        }
    val pool = context(context) {
        pool(100) {
            {
                inventorySlot(this@Inventory) {
                    onClick {
                        invokeClick(this)
                    }
                    tooltip = it.popup {
                        hideCursor = true
                        followCursor = true
                        visible = { this@inventorySlot.occupied }
                    }
                }
            }
        }
    }

    init {
        repeat(MinCells) { // fill inventory with minimal cells to display
            addSlot()
        }
    }

    private fun updateSlots() {
        slots.forEachIndexed { index, slot ->
            slot.isDisabled = index > maxSlots - 1
        }
    }

    private val slotListeners: MutableList<InventorySlot<T>.() -> Unit> = mutableListOf()

    @Scene2dCallbackDsl
    fun onSlotClick(onClick: InventorySlot<T>.() -> Unit) {
        slotListeners.add(onClick)
    }

    internal fun invokeClick(slot: InventorySlot<T>) {
        slotListeners.forEach {
            slot.it()
        }
    }

    internal fun onSlotRemoved() {
        updateSlots()
    }

    @Scene2dCallbackDsl
    fun addItem(data: InventorySlot.Data<T>): InventorySlot<T> {
        var available = slots.find {
            !it.occupied
        }
        if (available == null)
            available = addSlot()

        available.setData(data)
        return available
    }

    @Scene2dCallbackDsl
    fun setData(data: List<InventorySlot.Data<T>>) {
        clean()
        data.forEach(::addItem)
        updateSlots()
    }

    @Scene2dCallbackDsl
    fun clean() {
        slots.forEach(InventorySlot<T>::clean)
    }

    @Scene2dCallbackDsl
    internal fun addSlot(): InventorySlot<T> {
        val slot = context(context) {
            with(pool) {
                grid.pull()
            }
        }
        updateSlots()
        return slot.userObject.cast()
    }
}

data class SortAction<T>(
    val text: TextManager,
    val action: (Inventory<T>) -> Unit
)

fun <T> sort(
    text: TextManager,
    action: (Inventory<T>) -> Unit
) = SortAction(text, action)

@Scene2dDsl
context(gui: GuiContext)
fun <S, T> KWidget<S>.inventory(
    sortActions: List<SortAction<T>>,
    init: (@Scene2dDsl Inventory<T>).() -> Unit = {},
) =
    guiWindow(translate { inventory }) { contentCell ->
        contentCell.top().grow()
        lateinit var grid: KGrid
        table {
            scroll({
                it.width(GridWidth)
                    .height(GridHeight + 10)
                    .align(Align.topLeft)
                    .fill()
            }) {
                grid(Columns, CellPadding) {
                    pad(15f)
                    align(Align.topLeft)
                    grid = this
                }
            }
        }
        val inventory = Inventory<T>(grid, gui)
        row()
        buttons {
            sortActions.forEach { sort ->
                mainButton(sort.text) {
                    it.left().expand()
                    onClick {
                        sort.action(inventory)
                    }
                }
            }
        }

        inventory.init()
    }

@Scene2dDsl
context(gui: GuiContext)
private fun KTable.buttons(build: (@Scene2dDsl KTable).() -> Unit = {}) {
    image(separatorHorizontal { this }) {
        it.fillX().padTop(1f).padBottom(1f).padLeft(10f).padRight(30f).growX()
    }
    row()
    table { bottomCell ->
        bottomCell.fill().padRight(20f)
        row()
        build()
    }
}

@Scene2dDsl
context(gui: GuiContext)
fun <S, T> KWidget<S>.inventorySlot(
    inventory: Inventory<T>,
    ref: (@Scene2dDsl InventorySlot<T>).(KTable) -> Unit = {}
) =
    slot { slotTable ->
        lateinit var grade: ImperativeHandler<KLabel, ImperativeActionDelegate>
        lateinit var count: ImperativeHandler<KLabel, ImperativeActionValue<String>>

        val slot = InventorySlot(inventory, slotTable, this, inventory::onSlotRemoved, { igrade ->
            grade {
                set(igrade)
            }
        }, {
            count {
                value = it
            }
        })
        slotTable.userObject = slot
        content {
            align(Align.right)
            //grade
            grade = delegate {
                label(string { get<ItemGrade?>()?.name ?: "" }, params = {
                    size = 30
                    borderWidth = 3f
                    borderColor = Color.BLACK
                    borderStraight = false
                }) {
                    onChange<ItemGrade?> { grade ->
                        color = grade?.color ?: Color.WHITE
                    }
                    autoupdate()
                    it.expandY().top().right()
                }
            }
            row()
            //count
            count = value("") {
                label(string { value }, params = {
                    size = 30
                    borderWidth = 3f
                    borderColor = Color.BLACK
                    borderStraight = false
                }) {
                    it.expandY().bottom().right()
                    autoupdate()
                }
            }
        }
        slot.ref(slotTable)
    }