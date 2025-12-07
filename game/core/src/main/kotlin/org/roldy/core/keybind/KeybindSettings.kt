package org.roldy.core.keybind

import com.badlogic.gdx.Input

class KeybindSettings {
    private val keybinds: MutableMap<KeybindName, Int> = defaultKeybinds.toMutableMap()

    operator fun get(name: KeybindName): Int? = keybinds[name]

    operator fun set(name: KeybindName, key: Int) {
        keybinds[name] = key
    }
}

val defaultKeybinds = mapOf(
    KeybindName.CameraZoomIn to Input.Keys.Q,
    KeybindName.CameraZoomOut to Input.Keys.E,
    KeybindName.MoveTo to Input.Buttons.LEFT,
)

val keybinds = KeybindSettings()