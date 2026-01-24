package org.roldy.state

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.roldy.core.compress
import org.roldy.core.decompress
import org.roldy.data.state.GameState
import java.io.File

class GameSaveManager(
    val file: File,
) {

    val json = Json {
        useArrayPolymorphism = true
    }

    fun save(gameState: GameState) {
        val str = json.encodeToString(gameState)
        file.writeBytes(str.compress())
    }

    fun load(): GameState =
        file.let {
            json.decodeFromString<GameState>(it.readBytes().decompress())
        }
}