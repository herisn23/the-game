package org.roldy.core.ui

import org.roldy.core.disposable.AutoDisposable
import org.roldy.core.ui.el.UIStage


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