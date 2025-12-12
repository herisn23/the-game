package org.roldy.state

import kotlinx.serialization.json.Json
import org.roldy.data.GameState
import java.io.File


fun load(newGame: ()->GameState): GameState =
    File("save_data.json").let {
        if (!it.exists()) {
            newGame()
        } else {
            Json.decodeFromString<GameState>(it.readText())
        }
    }
