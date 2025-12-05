package org.roldy.keybind

interface KeybindProcessor {
    val settings: KeybindSettings

    fun keyDown(keycode: Int): Boolean = false
    fun keyUp(keycode: Int): Boolean = false
    fun keyTyped(character: Char): Boolean = false
    fun mouseMoved(screenX: Int, screenY: Int): Boolean = false
    fun scrolled(amountX: Float, amountY: Float): Boolean = false
}