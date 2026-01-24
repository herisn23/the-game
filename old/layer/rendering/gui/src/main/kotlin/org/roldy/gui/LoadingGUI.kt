package org.roldy.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Align
import org.roldy.core.i18n.I18N
import org.roldy.gui.general.progressBar.LoadingBarDelegate
import org.roldy.gui.general.progressBar.loadingBar
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.gui.Gui
import org.roldy.rendering.g2d.gui.el.table

class LoadingGUI : AutoDisposableAdapter(), Gui {
    private lateinit var progressBar: LoadingBarDelegate
    override val stage = gui(1f, createDefaultGuiContext()) { gui ->
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
        with(progressBar) {
            this.progress.set(progress)
            this.loadingText.set(translate { key })
        }
    }

}