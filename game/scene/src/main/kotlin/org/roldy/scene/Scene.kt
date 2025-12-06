package org.roldy.scene

interface Scene {
    context(delta: Float)
    fun render()
    fun onShow() {}
    fun onHide() {}
    fun onDestroy() {}
}