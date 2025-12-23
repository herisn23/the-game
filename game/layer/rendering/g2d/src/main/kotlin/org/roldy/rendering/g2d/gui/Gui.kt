package org.roldy.rendering.g2d.gui

import org.roldy.rendering.g2d.disposable.AutoDisposable

@Scene2dDsl
interface Gui: AutoDisposable {
    context(delta:Float)
    fun render()
    fun resize(width: Int, height: Int)
}