package org.roldy.scene

import com.badlogic.gdx.utils.Disposable

interface Scene: Disposable {
    context(delta: Float)
    fun render()
    fun onShow() {}
    fun onHide() {}
    fun onDestroy() {}
}