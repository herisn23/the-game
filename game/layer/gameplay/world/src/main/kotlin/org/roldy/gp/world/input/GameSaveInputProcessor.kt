package org.roldy.gp.world.input

import com.badlogic.gdx.InputAdapter
import org.roldy.core.keybind.KeybindName
import org.roldy.core.keybind.KeybindSettings
import org.roldy.core.logger
import org.roldy.data.state.GameState
import org.roldy.state.GameSaveManager

class GameSaveInputProcessor(
    val settings: KeybindSettings,
    val state: GameState,
    val gameSaveManager: GameSaveManager
) : InputAdapter() {
    override fun keyDown(keycode: Int): Boolean {
        if (keycode != settings[KeybindName.QuickSave]) return false
        gameSaveManager.save(state)
        logger.info { "Game saved !!!" }
        return true
    }
}