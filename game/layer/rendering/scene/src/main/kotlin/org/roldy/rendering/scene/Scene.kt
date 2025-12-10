package org.roldy.rendering.scene

import com.badlogic.gdx.utils.Disposable
import org.roldy.rendering.g2d.disposable.AutoDisposable

interface Scene: Disposable, AutoDisposable {
    context(delta: Float)
    fun render()
    fun onShow() {}
    fun onHide() {}
    fun onDestroy() {}
}