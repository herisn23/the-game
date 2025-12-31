package org.roldy.gui.widget

import org.roldy.data.state.HarvestableState
import org.roldy.gui.GuiContext
import org.roldy.gui.general.LabelActions
import org.roldy.gui.general.WindowActions
import org.roldy.gui.general.button.TextButtonActions
import org.roldy.gui.general.button.mainButton
import org.roldy.gui.general.label
import org.roldy.gui.general.window
import org.roldy.gui.translate
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.onClick
import kotlin.properties.Delegates


class HarvestingWindow(
    private val uiWindow: WindowActions,
    private val remainingLabel: LabelActions,
    private val harvestedLabel: LabelActions,
    private val harvestableLabel: LabelActions,
    val harvestButton: TextButtonActions,
    collectButton: TextButtonActions
) {
    var onHarvest: (() -> Unit)? = null
    var onCollect: (() -> Unit)? = null
    var state: HarvestableState? by Delegates.observable(null) { _, _, newValue ->
        if (newValue != null) {
            val mineLocKey = newValue.harvestable.type.locKey
            remainingLabel.setText(translate { mine_supplies.arg("amount", newValue.refreshing.supplies) })
            harvestableLabel.setText(translate { harvestable[newValue.harvestable.locKey] })
            harvestedLabel.setText(translate { harvesting_amount[mineLocKey].arg("amount", newValue.harvested) })
            uiWindow.title.setText(translate { harvesting[mineLocKey] })
            harvestButton.setText(translate { harvesting_begin[mineLocKey] })
            uiWindow.rebuild()
        }
    }

    init {
        harvestButton.button.onClick {
            onHarvest?.invoke()
        }
        collectButton.button.onClick {
            onCollect?.invoke()
        }
    }

    fun close(onClose: (HarvestingWindow) -> Unit) {
        uiWindow.close {
            onClose(this)
        }
    }

    fun open() {
        uiWindow.open()
    }

    fun clean() {
        onHarvest = null
        onCollect = null
        state = null
    }
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.harvestingWindow(init: (HarvestingWindow) -> Unit): WindowActions {
    lateinit var mine: TextButtonActions
    lateinit var collect: TextButtonActions
    lateinit var remaining: LabelActions
    lateinit var harvested: LabelActions
    lateinit var harvestable: LabelActions
    val window = window {
        setPosition(gui.stage.width / 2, gui.stage.height / 2)
        label {
            harvestable = this
        }
        label {
            remaining = this
            autoupdate()
        }
        row()
        label {
            harvested = this
            autoupdate()
        }
        row()
        mine = mainButton()
        collect = mainButton(translate { harvesting_collect })
    }
    init(HarvestingWindow(window, remaining, harvested, harvestable, mine, collect))
    return window
}