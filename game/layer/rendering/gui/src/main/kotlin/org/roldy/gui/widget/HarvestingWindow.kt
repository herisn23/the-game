package org.roldy.gui.widget

import org.roldy.core.i18n.Strings
import org.roldy.data.state.HarvestableState
import org.roldy.gui.GuiContext
import org.roldy.gui.general.LabelActions
import org.roldy.gui.general.button.TextButtonActions
import org.roldy.gui.general.button.mainButton
import org.roldy.gui.general.label
import org.roldy.gui.general.window
import org.roldy.gui.translate
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.onClick
// Delegate
class HarvestingWindowDelegate : ImperativeActionDelegate() {

    object State : ImperativeValue<HarvestableState>
    object Clean : ImperativeFunction<Unit, Unit>
    object Open : ImperativeFunction<Unit, Unit>
    object Close : ImperativeFunction<Unit, Unit>
    object Harvest : ImperativeValue<ImperativeRunnableValue>
    object Collect : ImperativeValue<ImperativeRunnableValue>
    object HarvestableRefreshingProgress : ImperativeValue<Float>
    object HarvestingRefreshingProgress : ImperativeValue<Float>

    val state = State
    val clean = Clean
    val open = Open
    val close = Close
    val harvest = Harvest
    val collect = Collect
    val harvestingProgress = HarvestingRefreshingProgress
    val harvestableProgress = HarvestableRefreshingProgress

    init {
        harvestingProgress init 0f
        harvestableProgress init 0f
    }
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.harvestingWindow(init: (HarvestingWindowDelegate) -> Unit) {
    lateinit var harvestButton: TextButtonActions
    lateinit var remaining: LabelActions
    lateinit var harvested: LabelActions
    lateinit var harvestable: LabelActions
    delegate(HarvestingWindowDelegate()) {

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

            harvestButton = mainButton {
                onClick {
                    harvest()
                }
            }
            mainButton(translate { harvesting_collect }) {
                onClick {
                    collect()
                }
            }
        }
        state { newValue ->
            val mineLocKey = newValue.harvestable.type.locKey
            remaining.setText(translate { mine_supplies.arg("amount", newValue.refreshing.supplies) })
            harvestable.setText(translate { Strings.harvestable[newValue.harvestable.locKey] })
            harvested.setText(translate { harvesting_amount[mineLocKey].arg("amount", newValue.harvested) })
            window.title.setText(translate { harvesting[mineLocKey] })
            harvestButton.setText(translate { harvesting_begin[mineLocKey] })
            window.rebuild()
        }

        clean {
            harvest.set { }
            collect.set { }
        }

        open {
            window.open()
        }

        close {
            window.close()
        }

        clean(Unit)
        init(this)
    }
}