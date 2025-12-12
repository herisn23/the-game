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
    KeybindName.QuickSave to Input.Keys.F5,
    KeybindName.QuickLoad to Input.Keys.F6
)

val keybinds = KeybindSettings()