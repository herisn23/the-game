package org.roldy.gui

import org.roldy.core.Vector2Int
import org.roldy.gui.general.button.mainButton
import org.roldy.gui.general.popup.tilePopup
import org.roldy.gui.widget.HarvestingWindow
import org.roldy.gui.widget.PlayerInventoryWindow
import org.roldy.gui.widget.harvestingWindow
import org.roldy.gui.widget.playerInventory
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.Gui
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.*


class WorldGUI(
    val onGameSaved: () -> Unit
) : AutoDisposableAdapter(), Gui {

    lateinit var tileTooltip: UIStandardPopup
    lateinit var harvestingWindow: HarvestingWindow
    lateinit var inventory: PlayerInventoryWindow
    lateinit var guiContext: GuiContext
    override val stage = gui(1f) { gui ->
        this@WorldGUI.guiContext = gui
        stack {
            name = "TilePopupStack"
            setLayoutEnabled(false)
            this@WorldGUI.tileTooltip = tilePopup()

        }
        harvestingWindow {
            this@WorldGUI.harvestingWindow = it
        }
        playerInventory {
            this@WorldGUI.inventory = it
        }
        table {
            mainButton("Inventory") {
                onClick {
                    this@WorldGUI.inventory.actions.toggle()
                }
            }
            mainButton("Save") {
                onClick {
                    this@WorldGUI.onGameSaved()
                }
            }
        }
    }

    @Scene2dDsl
    fun hideTileInfo() {
        tileTooltip.hide()
    }

    @Scene2dDsl
    fun showTileInfo(
        content: context(GuiContext) UITable.(UIPopup) -> Unit,
        follow: () -> Vector2Int
    ): UIStandardPopup {
        context(guiContext) {
            tileTooltip.show {
                content(it)
            }
            tileTooltip.position = follow
        }
        return tileTooltip
    }
}