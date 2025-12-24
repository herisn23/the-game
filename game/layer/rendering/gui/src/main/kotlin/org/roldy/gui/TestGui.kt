package org.roldy.gui

import com.badlogic.gdx.Gdx
import org.roldy.core.i18n.t
import org.roldy.core.logger
import org.roldy.core.utils.sequencer
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.Gui
import org.roldy.rendering.g2d.gui.onClick
import org.roldy.rendering.g2d.gui.table
import java.util.*

class TestGui : AutoDisposableAdapter(), Gui {


    val stage = gui(.5f) { gui ->
        val stage = this
        val locales by sequencer {
            gui.i18n.languages
        }

        logger.debug {
            gui.i18n.languages.joinToString(",")
        }

        table {
            setFillParent(true)
            setSize(stage.width, stage.height)
            setPosition(0f, 0f)

            val btn = mainButton(t { lang }) {
                onClick {
                    gui.i18n.locale = locales.next()
                }
            }

            mainButton(t { lang }) {
                onClick(btn::remove)
            }
//
////            row()
//
//            button("3")
//            button("4")
//            simpleButton()
//            simpleButton()

//            pack()
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)

        // Force update mouse position after resize
        val mouseX = Gdx.input.getX();
        val mouseY = Gdx.input.getY();
        stage.mouseMoved(mouseX, mouseY);
    }

    context(delta: Float)
    override fun render() {
        stage.act(delta)
        stage.draw()
    }
}