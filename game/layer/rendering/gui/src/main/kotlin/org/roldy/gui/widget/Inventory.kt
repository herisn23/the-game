package org.roldy.gui.widget

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import org.roldy.core.cast
import org.roldy.data.item.ItemGrade
import org.roldy.gui.*
import org.roldy.gui.general.*
import org.roldy.gui.general.button.mainButton
import org.roldy.gui.general.tooltip.tooltip
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.*
import kotlin.properties.Delegates

private const val MaxamountDisplay = 99
private const val Columns = 8
private const val Rows = 8
private const val CellPadding = 5f
private const val GridWidth = Columns * SlotSize + Columns * (CellPadding * 2) + SlotSize + CellPadding * 2 + 55
private const val GridHeight = Rows * SlotSize - SlotSize / 2
private const val MinCells = 48

@Scene2dCallbackDsl
data class InventorySlot<D>(
    private val inventory: Inventory<D>,
    val table: UITable,
    private val slot: Slot,
    private val onRemoved: () -> Unit,
    private val setGrade: (ItemGrade?) -> Unit,
    private val setamount: (String) -> Unit,
    private val setLock: (Boolean) -> Unit
) : PoolItem {

    data class Data<D>(
        val icon: Drawable,
        val grade: ItemGrade? = null,
        val amount: Int? = null,
        val lock: Boolean = false,
        val index: Int,
        val data: D,
        val tooltip: (@Scene2dCallbackDsl UITable).(Data<D>) -> Unit
    )

    val gridIndex get() = inventory.slots.indexOf(this)

    var slotData: Data<D>? by Delegates.observable<Data<D>?>(null) { _, _, newValue ->
        if (newValue != null) {
            setGrade(newValue.grade)
            setLock(newValue.lock)
            setamount(newValue.amount.clampToString())
            setIcon(newValue.icon)
        } else {
            setGrade(null)
            setLock(false)
            setamount("")
            setIcon(null)
        }
    }
    internal lateinit var tooltip: UIContextualTooltip

    val occupied get() = slotData != null

    var isDisabled: Boolean
        get() = slot.isDisabled
        set(value) {
            slot.isDisabled = value
        }

    private fun Int?.clampToString() =
        this?.let {
            when (this > MaxamountDisplay) {
                true -> "99+"
                false -> toString()
            }
        } ?: ""

    fun onClick(onClick: InventorySlot<D>.(InputEvent) -> Unit) {
        slot.onClick {
            this.onClick(it)
        }
    }

    fun setData(data: Data<D>) {
        this.slotData = data
        tooltip {
            with(data) {
                tooltip(data)
            }
        }
    }

    fun setIcon(drawable: Drawable?) {
        slot.setIcon(drawable)
    }


    fun tooltip(content: UITable.() -> Unit) {
        this.tooltip.content {
            content()
        }
    }

    override fun clean() {
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
    amount: Int? = null,
    lock: Boolean = false,
    index: Int,
    data: D,
    tooltip: (@Scene2dCallbackDsl UITable).(InventorySlot.Data<D>) -> Unit = {}
) = InventorySlot.Data(icon, grade, amount, lock, index, data, tooltip)


class Inventory<D>(
    private val grid: UIGrid,
    private val scroll: UIScrollPane,
    private val context: GuiContext
) {
    private val slotListeners: MutableList<D.(InputEvent) -> Unit> = mutableListOf()
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
                inventorySlot(this@Inventory) { slot ->
                    onClick {
                        slotData?.run {
                            invokeClick(data, it)
                        }
                    }
                    tooltip = slot.tooltip {
                        animate = false
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
    fun onSlotClick(onClick: D.(InputEvent) -> Unit) {
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

    internal fun onSlotRemoved() {
        updateSlots()
    }

    fun findSuitableSlot(data: InventorySlot.Data<D>): InventorySlot<D>? {
        val available = slots.filterIndexed { index, slot ->
            !slot.occupied && index == data.index
        }
        return available.firstOrNull()
    }

    private fun addItem(data: InventorySlot.Data<D>): InventorySlot<D> {
        var suitable = findSuitableSlot(data)
        if (suitable == null)
            suitable = addSlot()

        suitable.setData(data)
        return suitable
    }

    fun setData(data: List<InventorySlot.Data<D>>) {
        clean()
        data.forEach {
            addItem(it)
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

    internal fun move(original: InventorySlot<D>, target: InventorySlot<D>) {
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

    internal fun invokeClick(slot: D, event: InputEvent) {
        slotListeners.forEach {
            slot.it(event)
        }
    }
}

data class SortAction<D>(
    val text: TextManager,
    val action: (Inventory<D>) -> Unit
)

@Scene2dDsl
context(gui: GuiContext)
fun <S, D> UIWidget<S>.inventory(
    init: (@Scene2dDsl Inventory<D>).() -> Unit = {},
) =
    window(translate { inventory }) { contentCell ->
        contentCell.top().grow()
        lateinit var grid: UIGrid
        lateinit var scroll: org.roldy.rendering.g2d.gui.el.UIScrollPane
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
private fun org.roldy.rendering.g2d.gui.el.UITable.buttons(build: (@Scene2dDsl org.roldy.rendering.g2d.gui.el.UITable).() -> Unit = {}) {
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
fun <S, D> UIWidget<S>.inventorySlot(
    inventory: Inventory<D>,
    ref: (@Scene2dDsl InventorySlot<D>).(UITable) -> Unit = {}
) =
    slot { slotTable ->
        lateinit var grade: Value<UILabel, ItemGrade?>
        lateinit var amount: Value<UILabel, String>
        lateinit var lock: Value<UILabel, Boolean>

        val slot = InventorySlot(
            inventory, slotTable, this, inventory::onSlotRemoved,
            { igrade ->
                grade {
                    value = igrade
                }
            },
            {
                amount {
                    value = it
                }
            }, {
                lock {
                    value = it
                }
            })

        inputListener {
            type(InputEvent.Type.enter) {
                slotTable.isTransform = true
                slotTable.setOrigin(Align.center)
                if (inventory.dragging)
                    action(
                        Actions.sequence(
                            Actions.scaleTo(1.1f, 1.1f, 0.2f, Interpolation.smooth)
                        )
                    )
            }
            type(InputEvent.Type.exit, InputEvent.Type.touchUp) {
                action(
                    Actions.sequence(
                        Actions.scaleTo(1f, 1f, 0.2f, Interpolation.smooth)
                    )
                )
            }
        }
        draggable<InventorySlot<D>> {
            fun highlight(alpha: Float) {
                this@slot.icon.color.a = alpha
                amount.ref.color.a = alpha
                grade.ref.color.a = alpha
                lock.ref.color.a = alpha
            }

            val prevData = slot.slotData
            canDrag = {
                slot.occupied// && slot.slotData?.lock == false
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
            //amount
            lock = value(false) {
                label(string { if (value) "L" else "" }, params = {
                    size = 30
                    borderWidth = 3f
                    borderColor = Color.BLACK
                    borderStraight = false
                }) {
                    it.expand().top().left()
                    autoupdate()
                }
            }
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
                    it.expand().top().right()
                }
            }
            row()
            //amount
            amount = value("") {
                label(string { value }, params = {
                    size = 30
                    borderWidth = 3f
                    borderColor = Color.BLACK
                    borderStraight = false
                }) {
                    it.expand().colspan(2).right().bottom()
                    autoupdate()
                }
            }
        }
        slot.ref(slotTable)
    }