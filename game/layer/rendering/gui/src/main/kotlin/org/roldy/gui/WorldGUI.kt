package org.roldy.gui

import com.badlogic.gdx.Gdx
import org.roldy.core.Vector2Int
import org.roldy.core.i18n.t
import org.roldy.core.utils.sequencer
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.*


class WorldGUI : AutoDisposableAdapter(), Gui {

    lateinit var worldTooltip: Pair<KClampingPopup, GuiContext>
    lateinit var tileTooltip: Pair<KStandardPopup, GuiContext>

    val stage = gui(.5f) { gui ->
        val stage = this
        val langs by sequencer { gui.i18n.languages }
        table {
            setFillParent(true)
            setSize(stage.width, stage.height)
            setPosition(0f, 0f)
//
            mainButton(t { lang }) {
//                onClick {
//                    gui.i18n.locale = langs.next()
//                }
            }

            simpleButton(t { close })
        }
        stack {
            setLayoutEnabled(false)
            this@WorldGUI.worldTooltip = worldAnchoredPopup() to gui
            this@WorldGUI.tileTooltip = tilePopup() to gui
        }
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