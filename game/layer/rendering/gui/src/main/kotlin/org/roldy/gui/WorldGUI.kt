package org.roldy.gui

import org.roldy.core.Vector2Int
import org.roldy.gui.general.popup.tilePopup
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.Gui
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIPopup
import org.roldy.rendering.g2d.gui.el.UIStandardPopup
import org.roldy.rendering.g2d.gui.el.UITable
import org.roldy.rendering.g2d.gui.el.stack


class WorldGUI : AutoDisposableAdapter(), Gui {

    lateinit var tileTooltip: Pair<UIStandardPopup, GuiContext>

    override val stage = gui(1f) { gui ->
        stack {
            name = "TilePopupStack"
            setLayoutEnabled(false)
            this@WorldGUI.tileTooltip = tilePopup() to gui
        }
        example()
    }

    @Scene2dDsl
    fun hideTileInfo() {
        tileTooltip.first.hide()
    }

    @Scene2dDsl
    fun showTileInfo(
        content: context(GuiContext) UITable.(UIPopup) -> Unit,
        follow: () -> Vector2Int
    ): UIStandardPopup {
        val (popup, context) = tileTooltip
        context(context) {
            popup.show {
                content(it)
            }
            popup.position = follow
        }
        return popup
    }
}