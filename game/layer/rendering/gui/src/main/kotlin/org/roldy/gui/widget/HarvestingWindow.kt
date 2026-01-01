package org.roldy.gui.widget

import org.roldy.core.i18n.Strings
import org.roldy.data.state.HarvestableState
import org.roldy.gui.CraftingIconTexturesType
import org.roldy.gui.GuiContext
import org.roldy.gui.general.LabelActions
import org.roldy.gui.general.button.TextButtonActions
import org.roldy.gui.general.button.smallButton
import org.roldy.gui.general.icon
import org.roldy.gui.general.label
import org.roldy.gui.general.progressBar.ProgressBarDelegate
import org.roldy.gui.general.progressBar.ProgressBarSize
import org.roldy.gui.general.progressBar.progressBar
import org.roldy.gui.general.window
import org.roldy.gui.translate
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.UIImage
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.onClick

object HarvestingWindowAction : ImperativeAction

// Delegate
class HarvestingWindowDelegate : ImperativeActionDelegate<HarvestingWindowAction>() {

    object State : ImperativeValue<HarvestableState, HarvestingWindowAction>
    object Clean : ImperativeFunction<Unit, Unit, HarvestingWindowAction>
    object Open : ImperativeFunction<Unit, Unit, HarvestingWindowAction>
    object Close : ImperativeFunction<Unit, Unit, HarvestingWindowAction>
    object Harvest : ImperativeValue<ImperativeRunnableValue, HarvestingWindowAction>
    object Collect : ImperativeValue<ImperativeRunnableValue, HarvestingWindowAction>
    object HarvestableRefreshingProgress : ImperativeValue<Float, HarvestingWindowAction>
    object HarvestingRefreshingProgress : ImperativeValue<Float, HarvestingWindowAction>

    val state = State
    val clean = Clean
    val open = Open
    val close = Close
    val harvest = Harvest
    val collect = Collect
    val harvestingProgress = HarvestingRefreshingProgress
    val harvestableProgress = HarvestableRefreshingProgress
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.harvestingWindow(init: (HarvestingWindowDelegate) -> Unit) {
    lateinit var harvestButton: TextButtonActions
    lateinit var remaining: LabelActions
    lateinit var harvested: LabelActions
    lateinit var harvestable: LabelActions
    lateinit var progressBar: ProgressBarDelegate
    lateinit var icon: UIImage
    delegate(HarvestingWindowDelegate()) {

        val window = window {
            setPosition(gui.stage.width / 2, gui.stage.height / 2)
            icon {
                icon = this
            }
            row()
            label {
                harvestable = this
            }
            row()
            label {
                remaining = this
                autoupdate()
            }
            row()
            label {
                harvested = this
                autoupdate()
            }
            progressBar(ProgressBarSize.Medium) {
                with(this@delegate) {
                    harvestingProgress {
                        with(this@progressBar) {
                            amount set it
                        }
                    }
                }
            }
            row()

            harvestButton = smallButton {
                onClick {
                    harvest()
                }
            }
            smallButton(translate { harvesting_collect }) {
                onClick {
                    collect()
                }
            }
        }

        state { newValue ->
            val mineLocKey = newValue.harvestable.type.key
            remaining.setText(translate { mine_supplies.arg("amount", newValue.refreshing.supplies) })
            harvestable.setText(translate { Strings.harvestable[newValue.harvestable.key] })
            harvested.setText(translate { harvesting_amount[mineLocKey].arg("amount", newValue.harvested) })
            window.title.setText(translate { harvesting[mineLocKey] })
            harvestButton.setText(translate { harvesting_begin[mineLocKey] })
            icon.drawable = gui.craftingIcons.drawable(newValue.harvestable, CraftingIconTexturesType.Background)
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