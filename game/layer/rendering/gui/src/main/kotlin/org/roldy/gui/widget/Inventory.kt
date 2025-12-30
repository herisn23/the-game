package org.roldy.gui.widget

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import org.roldy.core.cast
import org.roldy.core.eventEnter
import org.roldy.core.eventExit
import org.roldy.core.eventTouchUp
import org.roldy.data.item.ItemGrade
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.gui.general.*
import org.roldy.gui.general.button.mainButton
import org.roldy.gui.general.tooltip.tooltip
import org.roldy.gui.separatorHorizontal
import org.roldy.gui.translate
import org.roldy.gui.widget.InventorySlot.Data
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.*
import kotlin.properties.Delegates

private const val MaxAmountDisplay = 99
private const val Columns = 8
private const val Rows = 8
private const val CellPadding = 5f
private const val GridWidth = Columns * SlotSize + Columns * (CellPadding * 2) + SlotSize + CellPadding * 2 + 55
private const val GridHeight = Rows * SlotSize - SlotSize / 2
private const val MinCells = 48

typealias SlotTooltip<D> = (@Scene2dCallbackDsl UIContextualTooltipContent).(Data<D>) -> Unit

@Scene2dCallbackDsl
data class InventorySlot<D>(
    private val inventory: Inventory<D>,
    val table: UITable,
    private val slot: Slot,
    private val onRemoved: () -> Unit,
    private val setGrade: (ItemGrade?) -> Unit,
    private val setAmount: (String?) -> Unit,
    private val setLock: (Boolean) -> Unit
) : PoolItem {

    data class Data<D>(
        var icon: Drawable,
        var grade: ItemGrade? = null,
        var amount: Int? = null,
        var lock: Boolean = false,
        var index: Int,
        var item: D,
        var tooltip: SlotTooltip<D>
    )

    val gridIndex get() = inventory.slots.indexOf(this)

    var data: Data<D>? by Delegates.observable<Data<D>?>(null) { _, _, newValue ->
        if (newValue != null) {
            setGrade(newValue.grade)
            setLock(newValue.lock)
            setAmount(newValue.amount.clampToString())
            setIcon(newValue.icon)
            tooltip {
                with(newValue) {
                    tooltip(this)
                }
            }
        } else {
            setGrade(null)
            setLock(false)
            setAmount("")
            setIcon(null)
        }
    }
    internal lateinit var tooltip: UIContextualTooltip

    val occupied get() = data != null

    var isDisabled: Boolean
        get() = slot.isDisabled
        set(value) {
            slot.isDisabled = value
        }

    private fun Int?.clampToString(): String? =
        this?.let {
            when (this > MaxAmountDisplay) {
                true -> "99+"
                false -> toString()
            }
        }

    fun onClick(onClick: InventorySlot<D>.(InputEvent) -> Unit) {
        slot.onClick {
            this.onClick(it)
        }
    }

    fun setIcon(drawable: Drawable?) {
        slot.setIcon(drawable)
    }


    fun tooltip(content: UIContextualTooltipContent.() -> Unit) {
        this.tooltip.content {
            content()
        }
    }

    override fun clean() {
        isDisabled = false
        data = null
        tooltip.clean()
    }

    fun reload(
    ) {
        data?.let {
            clean()
            data = it
        }
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
    tooltip: SlotTooltip<D> = {}
) = Data(icon, grade, amount, lock, index, data, tooltip)


class Inventory<D>(
    private val grid: UIGrid,
    private val scroll: UIScrollPane,
    private val context: GuiContext
) {
    private val slotListeners: MutableList<D.(Pair<InventorySlot<D>, InputEvent>) -> Unit> = mutableListOf()
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
            it.getData()
        }
    val pool = context(context) {
        pool(100) {
            {
                inventorySlot(this@Inventory) { table ->
                    onClick {
                        invokeClick(this, it)
                    }
                    tooltip = table.tooltip {
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
    fun onSlotClick(onClick: D.(Pair<InventorySlot<D>, InputEvent>) -> Unit) {
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

        suitable.data = data
        return suitable
    }

    fun setData(data: List<Data<D>>) {
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
        val targetData = target.data
        val originalData = original.data

        original.clean()
        target.clean()

        targetData?.let {
            original.data = it
        }
        originalData?.let {
            target.data = it
        }
    }

    internal fun invokeClick(slot: InventorySlot<D>, event: InputEvent) {
        slotListeners.forEach { call ->
            slot.data?.item?.call(slot to event)
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
    window(translate { inventory }, "Inventory") { contentCell ->
        contentCell.top().grow()
        lateinit var grid: UIGrid
        lateinit var scroll: UIScrollPane
        table {
            scroll({
                scroll = this
                it.width(GridWidth)
                    .height(GridHeight + 10)
                    .align(Align.topLeft)
                    .fill()
            }) {

                grid(Columns, CellPadding) {
                    this.pad(15f)
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
private fun UITable.buttons(build: (@Scene2dDsl UITable).() -> Unit = {}) {
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

private fun slotDefaults(): FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit =
    {
        size = 30
        borderWidth = 3f
        borderColor = Color.BLACK
        borderStraight = false
    }

@Scene2dDsl
context(gui: GuiContext)
fun <S, D> UIWidget<S>.inventorySlot(
    inventory: Inventory<D>,
    ref: (@Scene2dDsl InventorySlot<D>).(UITable) -> Unit = {}
) =
    slot { slotTable ->
        lateinit var grade: Value<UILabel, ItemGrade?>
        lateinit var amount: Value<UILabel, String?>
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
            type(eventEnter) {
                slotTable.isTransform = true
                slotTable.setOrigin(Align.center)
                if (inventory.dragging)
                    action(
                        Actions.sequence(
                            Actions.scaleTo(1.1f, 1.1f, 0.2f, Interpolation.smooth)
                        )
                    )
            }
            type(eventExit, eventTouchUp) {
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

            val prevData = slot.data
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
                    slot.data = it
                }
            }
            onDrop = {
                inventory.move(slot, it)
            }
        }

        slotTable.setData(slot)

        content {
            //amount
            lock = value(false) {
                label(slotDefaults()) {
                    onChange {
                        if (value) {
                            setText("L")
                        } else {
                            setText(null)
                        }
                    }
                    it.expand().top().left()
                }
            }
            //grade
            grade = value(null) {
                gradeLabel(slotDefaults()) {
                    onChange(::setGrade)
                    it.expand().top().right()
                }
            }
            row()
            //amount
            amount = value(null) {
                label(slotDefaults()) {
                    onChange {
                        setText(value)
                    }
                    it.expand().colspan(2).right().bottom()
                }
            }
        }
        slot.ref(slotTable)
    }