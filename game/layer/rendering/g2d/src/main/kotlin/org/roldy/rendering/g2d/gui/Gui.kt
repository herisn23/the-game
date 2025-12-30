package org.roldy.rendering.g2d.gui

import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.gui.el.UIStage

@Scene2dDsl
interface Gui : AutoDisposable {
    val stage: UIStage

    fun resize(width: Int, height: Int) {
        stage.resize(width, height)
    }

    context(delta: Float)
    fun render() {
        stage.act(delta)
        stage.draw()
    }
}