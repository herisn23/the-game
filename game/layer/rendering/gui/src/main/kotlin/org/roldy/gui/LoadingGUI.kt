package org.roldy.gui

import com.badlogic.gdx.utils.Align
import org.roldy.core.i18n.I18N
import org.roldy.gui.general.LabelActions
import org.roldy.gui.general.label
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.Gui
import org.roldy.rendering.g2d.gui.el.table

class LoadingGUI : AutoDisposableAdapter(), Gui {
    private lateinit var loadingText: LabelActions

    override val stage = gui(1f) { gui ->
        table {
            setFillParent(true)
            align(Align.center)
            label(translate { loading }, 100)
            row()
            label(100) {
                this@LoadingGUI.loadingText = this
                setText("Loading...")
            }
        }
    }

    fun setProgress(progress: Float, key: I18N.Key) {
        loadingText.setText(translate { key })
    }

}