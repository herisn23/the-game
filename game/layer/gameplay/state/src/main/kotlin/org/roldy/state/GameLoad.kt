package org.roldy.state

import kotlinx.serialization.json.Json
import org.roldy.data.state.GameState
import java.io.File

val json = Json {
    ignoreUnknownKeys = true
}

fun load(newGame: () -> GameState): GameState =
    File("save_data.json").let {
        if (!it.exists()) {
            newGame()
        } else {
            json.decodeFromString<GameState>(it.readText())
        }
    }
