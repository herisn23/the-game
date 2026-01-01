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

interface HarvestingWindowAction : ImperativeAction
typealias HarvestingWindowDelegate = ImperativeActionDelegate<HarvestingWindowAction>

// Actions
object State : ImperativeValue<HarvestableState, HarvestingWindowAction>
object Clean : ImperativeFunction<Unit, Unit, HarvestingWindowAction>
object Open : ImperativeFunction<Unit, Unit, HarvestingWindowAction>
object Close : ImperativeFunction<Unit, Unit, HarvestingWindowAction>
object Harvest : ImperativeValue<ImperativeRunnableValue, HarvestingWindowAction>
object Collect : ImperativeValue<ImperativeRunnableValue, HarvestingWindowAction>
@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.harvestingWindow(init: (HarvestingWindowDelegate) -> Unit) {
    lateinit var harvest: TextButtonActions
    lateinit var remaining: LabelActions
    lateinit var harvested: LabelActions
    lateinit var harvestable: LabelActions
    delegate {
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

            harvest = mainButton {
                onClick {
                    Harvest()
                }
            }
            mainButton(translate { harvesting_collect }) {
                onClick {
                    Collect()
                }
            }
        }
        State { newValue ->
            val mineLocKey = newValue.harvestable.type.locKey
            remaining.setText(translate { mine_supplies.arg("amount", newValue.refreshing.supplies) })
            harvestable.setText(translate { Strings.harvestable[newValue.harvestable.locKey] })
            harvested.setText(translate { harvesting_amount[mineLocKey].arg("amount", newValue.harvested) })
            window.title.setText(translate { harvesting[mineLocKey] })
            harvest.setText(translate { harvesting_begin[mineLocKey] })
            window.rebuild()
        }

        Clean {

        }

        Open {
            window.open()
        }

        Close {
            window.close()
        }

        init(this)
    }
}