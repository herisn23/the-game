package org.roldy.scene

import com.badlogic.gdx.utils.Disposable
import org.roldy.core.disposable.AutoDisposable

interface Scene: Disposable, AutoDisposable {
    context(delta: Float)
    fun render()
    fun onShow() {}
    fun onHide() {}
    fun onDestroy() {}
}