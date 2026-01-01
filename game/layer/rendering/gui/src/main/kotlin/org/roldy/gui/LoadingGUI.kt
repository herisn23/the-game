package org.roldy.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Align
import org.roldy.core.i18n.I18N
import org.roldy.gui.general.progressBar.LoadingText
import org.roldy.gui.general.progressBar.Progress
import org.roldy.gui.general.progressBar.loadingBar
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.Gui
import org.roldy.rendering.g2d.gui.ImperativeActionDelegate
import org.roldy.rendering.g2d.gui.el.table

class LoadingGUI : AutoDisposableAdapter(), Gui {
    private lateinit var progressBar: ImperativeActionDelegate
    override val stage = gui(1f) { gui ->
        Gdx.input.inputProcessor = this
        table {
            pad(100f)
            setFillParent(true)
            align(Align.bottom)
            row()
            loadingBar {
                this@LoadingGUI.progressBar = this
            }
        }
    }

    fun setProgress(progress: Float, key: I18N.Key) {
        progressBar.set(Progress, progress)
        progressBar.set(LoadingText, translate { key })
    }

}