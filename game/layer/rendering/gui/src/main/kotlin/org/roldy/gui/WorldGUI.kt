package org.roldy.gui

import com.badlogic.gdx.Gdx
import org.roldy.core.Vector2Int
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.*


class WorldGUI : AutoDisposableAdapter(), Gui {

    lateinit var worldTooltip: Pair<KPopup<Unit>, GuiContext>

    val stage = gui(.5f) { gui ->
        val stage = this
        table {
            setFillParent(true)
            setSize(stage.width, stage.height)
            setPosition(0f, 0f)
        }
        stack {
            setLayoutEnabled(false)
            this@WorldGUI.worldTooltip = worldPopup() to gui
        }
    }

    @Scene2dDsl
    fun showTileInfo(positon: Vector2Int, text: String): KPopup<Unit> {
        val (popup, context) = worldTooltip
        return context(context, popup) {
            with(popup) {
                show(positon) {
                    label({ text })
                }
            }
            this@WorldGUI.worldTooltip.first
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)

        // Force update mouse position after resize
        val mouseX = Gdx.input.x
        val mouseY = Gdx.input.y
        stage.mouseMoved(mouseX, mouseY);
    }

    context(delta: Float)
    override fun render() {
        stage.act(delta)
        stage.draw()
    }
}