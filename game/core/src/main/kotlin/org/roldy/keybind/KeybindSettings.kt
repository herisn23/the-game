package org.roldy.keybind

import com.badlogic.gdx.Input

class KeybindSettings {
    private val keybinds: Map<KeybindName, Int> = mapOf(
        KeybindName.CameraZoomIn to Input.Keys.Q,
        KeybindName.CameraZoomOut to Input.Keys.E,
    )

    operator fun get(name: KeybindName): Int = keybinds.getValue(name)
}

val keybinds = KeybindSettings()