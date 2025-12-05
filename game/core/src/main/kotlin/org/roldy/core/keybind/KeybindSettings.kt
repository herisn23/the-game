package org.roldy.keybind

import com.badlogic.gdx.Input
import org.roldy.core.keybind.KeybindName

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
    KeybindName.MoveUp to Input.Keys.W,
    KeybindName.MoveDown to Input.Keys.S,
    KeybindName.MoveLeft to Input.Keys.A,
    KeybindName.MoveRight to Input.Keys.D,
)

val keybinds = KeybindSettings()