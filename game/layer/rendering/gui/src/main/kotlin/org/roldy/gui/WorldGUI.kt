package org.roldy.gui

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.Vector2Int
import org.roldy.gui.general.button.mainButton
import org.roldy.gui.general.button.smallButton
import org.roldy.gui.general.popup.tilePopup
import org.roldy.gui.widget.HarvestingWindowDelegate
import org.roldy.gui.widget.PlayerInventoryWindow
import org.roldy.gui.widget.harvestingWindow
import org.roldy.gui.widget.playerInventory
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.Gui
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.*


class WorldGUI(
    craftingIcons: TextureAtlas,
    val onGameSaved: () -> Unit
) : AutoDisposableAdapter(), Gui {

    lateinit var tileTooltip: UIStandardPopup
    lateinit var harvestingWindow: HarvestingWindowDelegate
    lateinit var inventory: PlayerInventoryWindow
    lateinit var guiContext: GuiContext
    lateinit var inventoryButton: UITextButton
    lateinit var teleportToStart: UITextButton
    lateinit var teleportToEnd: UITextButton

    override val stage = gui(1f, createWorldGuiContext(craftingIcons)) { gui ->
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
                this@WorldGUI.inventoryButton = this
            }
            mainButton("Save") {
                onClick {
                    this@WorldGUI.onGameSaved()
                }
            }
            smallButton("Teleport to start") {
                this@WorldGUI.teleportToStart = this
            }
            smallButton("Teleport to end") {
                this@WorldGUI.teleportToEnd = this
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

    fun harvestingWindow(run: HarvestingWindowDelegate.() -> Unit) {
        with(harvestingWindow) {
            run()
        }
    }
}