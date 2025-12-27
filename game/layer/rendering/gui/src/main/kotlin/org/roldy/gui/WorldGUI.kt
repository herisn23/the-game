package org.roldy.gui

import org.roldy.core.Vector2Int
import org.roldy.gui.general.popup.tilePopup
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.UIPopup
import org.roldy.rendering.g2d.gui.el.UIStandardPopup
import org.roldy.rendering.g2d.gui.el.stack


class WorldGUI : AutoDisposableAdapter(), Gui {

    lateinit var tileTooltip: Pair<UIStandardPopup, GuiContext>

    val stage = gui(1f) { gui ->
        stack {
            setLayoutEnabled(false)
            this@WorldGUI.tileTooltip = tilePopup() to gui
        }
        example()
    }

    @Scene2dDsl
    fun showTileInfo(
        content: context(GuiContext) org.roldy.rendering.g2d.gui.el.UITable.(UIPopup) -> Unit,
        follow: () -> Vector2Int
    ): UIStandardPopup {
        val (popup, context) = tileTooltip
        context(context, popup) {
            with(popup) {
                show {
                    content(it)
                }
                position = follow
            }
        }
        return popup
    }

    override fun resize(width: Int, height: Int) {
        stage.resize(width, height)
    }

    context(delta: Float)
    override fun render() {
        stage.act(delta)
        stage.draw()
    }
}