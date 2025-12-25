package org.roldy.gui

import org.roldy.core.Vector2Int
import org.roldy.gui.popup.globalPopup
import org.roldy.gui.popup.tilePopup
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.*


class WorldGUI : AutoDisposableAdapter(), Gui {

    lateinit var worldTooltip: Pair<KClampingPopup, GuiContext>
    lateinit var tileTooltip: Pair<KStandardPopup, GuiContext>

    val stage = gui(1f) { gui ->
        val stage = this
        stack {
            setLayoutEnabled(false)
            this@WorldGUI.worldTooltip = globalPopup() to gui
            this@WorldGUI.tileTooltip = tilePopup() to gui
        }
        example()
    }

    @Scene2dDsl
    fun showTileInfo(content: context(GuiContext) KTable.(KPopup) -> Unit, follow: () -> Vector2Int): KStandardPopup {
        val (popup, context) = tileTooltip
        context(context, popup) {
            with(popup) {
                show {
                    content(it)
                }
                followPosition = follow
            }
        }
        return popup
    }

    override fun resize(width: Int, height: Int) {
        stage.resize(width, height)

//        // Force update mouse position after resize
//        val mouseX = Gdx.input.x
//        val mouseY = Gdx.input.y
//        stage.mouseMoved(mouseX, mouseY);
    }

    context(delta: Float)
    override fun render() {
        stage.act(delta)
        stage.draw()
    }
}