package org.roldy.gameplay.world.input

import com.badlogic.gdx.InputAdapter
import org.roldy.core.keybind.KeybindName
import org.roldy.core.keybind.KeybindSettings
import org.roldy.core.logger
import org.roldy.data.state.GameState
import org.roldy.state.save

class GameSaveInputProcessor(
    val settings: KeybindSettings,
    val state: GameState
): InputAdapter() {
    override fun keyDown(keycode: Int): Boolean {
        if (keycode != settings[KeybindName.QuickSave]) return false

        state.save()
        logger.info { "Game saved !!!" }
        return true
    }
}