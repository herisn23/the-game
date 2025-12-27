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
data class InventorySlot<D>(
    private val inventory: Inventory<D>,
    private val table: KTable,
    private val slot: Slot,
    private val onRemoved: () -> Unit,
    private val setGrade: (ItemGrade?) -> Unit,
    private val setCount: (String) -> Unit
) : PoolItem {
    val gridIndex get() = inventory.slots.indexOf(this)

    var slotData: Data<D>? = null
    internal lateinit var tooltip: KContextualPopup

    data class Data<D>(
        val icon: Drawable,
        val grade: ItemGrade? = null,
        val count: Int? = null,
        val index: Int,
        val data: D,
        val tooltip: (@Scene2dCallbackDsl KTable).(Data<D>) -> Unit
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

    fun onClick(onClick: InventorySlot<D>.() -> Unit) {
        slot.onClick {
            this.onClick()
        }
    }

    fun setData(data: Data<D>) {
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

fun <D> data(
    icon: Drawable,
    grade: ItemGrade? = null,
    count: Int? = null,
    index: Int,
    data: D,
    tooltip: (@Scene2dCallbackDsl KTable).(InventorySlot.Data<D>) -> Unit = {}
) = InventorySlot.Data(icon, grade, count, index, data, tooltip)


class Inventory<D>(
    private val grid: KGrid,
    private val scroll: KScrollPane,
    private val context: GuiContext
) {
    private val slotListeners: MutableList<InventorySlot<D>.() -> Unit> = mutableListOf()
    private val slotPositionChangedListeners: MutableList<(from: InventorySlot<D>, to: InventorySlot<D>) -> Unit> =
        mutableListOf()
    internal val sortActions: MutableList<SortAction<D>> = mutableListOf()
    var dragging by Delegates.observable(false) { _, _, newValue ->
        scroll.setFlickScroll(!newValue)
        if (newValue) {
            scroll.cancel()
        }
    }
    var maxSlots: Int by Delegates.observable(0) { _, _, _ ->
        updateSlots()
        if (slots.size < maxSlots) {
            addSlot()
        }
    }

    val slots: List<InventorySlot<D>>
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
                        visible = { this@inventorySlot.occupied && !dragging }
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

    @Scene2dCallbackDsl
    fun onSlotClick(onClick: InventorySlot<D>.() -> Unit) {
        slotListeners.add(onClick)
    }

    @Scene2dCallbackDsl
    fun onSlotPositionChanged(onClick: (from: InventorySlot<D>, to: InventorySlot<D>) -> Unit) {
        slotPositionChangedListeners.add(onClick)
    }

    @Scene2dCallbackDsl
    fun sort(
        text: TextManager,
        action: (Inventory<D>) -> Unit
    ) {
        sortActions.add(SortAction(text, action))
    }

    internal fun invokeClick(slot: InventorySlot<D>) {
        slotListeners.forEach {
            slot.it()
        }
    }

    internal fun onSlotRemoved() {
        updateSlots()
    }

    fun findSuitableSlot(data: InventorySlot.Data<D>, sorted: Boolean): InventorySlot<D>? {
        val available = slots.filterIndexed { index, slot ->
            !slot.occupied && (index == data.index || sorted)
        }
        return available.firstOrNull()
    }

    private fun addItem(data: InventorySlot.Data<D>, sorted: Boolean): InventorySlot<D> {
        var suitable = findSuitableSlot(data, sorted)
        if (suitable == null)
            suitable = addSlot()

        suitable.setData(data)
        return suitable
    }

    fun setData(data: List<InventorySlot.Data<D>>, sorted: Boolean = true) {
        clean()
        data.forEach {
            addItem(it, sorted)
        }
        updateSlots()
    }

    fun clean() {
        slots.forEach(InventorySlot<D>::clean)
    }

    internal fun addSlot(): InventorySlot<D> {
        val slot = context(context) {
            with(pool) {
                grid.pull()
            }
        }
        updateSlots()
        return slot.userObject.cast()
    }

    fun move(original: InventorySlot<D>, target: InventorySlot<D>) {
        if (target.isDisabled) return
        slotPositionChangedListeners.forEach {
            it(original, target)
        }
        val targetData = target.slotData
        val originalData = original.slotData

        original.clean()
        target.clean()

        targetData?.let {
            original.setData(it)
        }
        originalData?.let {
            target.setData(it)
        }
    }
}

data class SortAction<D>(
    val text: TextManager,
    val action: (Inventory<D>) -> Unit
)

@Scene2dDsl
context(gui: GuiContext)
fun <S, D> KWidget<S>.inventory(
    init: (@Scene2dDsl Inventory<D>).() -> Unit = {},
) =
    guiWindow(translate { inventory }) { contentCell ->
        contentCell.top().grow()
        lateinit var grid: KGrid
        lateinit var scroll: KScrollPane
        table {
            scroll({
                scroll = this
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
        val inventory = Inventory<D>(grid, scroll, gui)
        inventory.init()
        row()
        buttons {
            inventory.sortActions.forEach { sort ->
                mainButton(sort.text) {
                    it.left().expand()
                    onClick {
                        sort.action(inventory)
                    }
                }
            }
        }
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
fun <S, D> KWidget<S>.inventorySlot(
    inventory: Inventory<D>,
    ref: (@Scene2dDsl InventorySlot<D>).(KTable) -> Unit = {}
) =
    slot { slotTable ->
        lateinit var grade: Value<KLabel, ItemGrade?>
        lateinit var count: Value<KLabel, String>

        val slot = InventorySlot(inventory, slotTable, this, inventory::onSlotRemoved, { igrade ->
            grade {
                value = igrade
            }
        }, {
            count {
                value = it
            }
        })
        draggable<InventorySlot<D>> {
            fun highlight(alpha: Float) {
                this@slot.icon.color.a = alpha
                count.ref.color.a = alpha
                grade.ref.color.a = alpha
            }

            val prevData = slot.slotData
            canDrag = {
                slot.occupied
            }
            onStart = {
                inventory.dragging = true
                highlight(.2f)
            }
            onEnd = {
                inventory.dragging = false
                highlight(1f)
                prevData?.let {
                    slot.setData(it)
                }
            }
            onDrop = {
                inventory.move(slot, it)
            }
        }

        slotTable.userObject = slot
        content {
            align(Align.right)
            //grade
            grade = value(null) {
                label(string { value?.name ?: "" }, params = {
                    size = 30
                    borderWidth = 3f
                    borderColor = Color.BLACK
                    borderStraight = false
                }) {
                    onChange { grade ->
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