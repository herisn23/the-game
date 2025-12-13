package org.roldy.state

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.roldy.core.compress
import org.roldy.core.coroutines.async
import org.roldy.core.decompress
import org.roldy.data.state.GameState
import java.io.File

val json = Json {
    useArrayPolymorphism = true
}

fun GameState.save() {
    async {
        val str = json.encodeToString(this)
        File("save_data.sav").writeBytes(str.compress())
    }
}

fun load(newGame: () -> GameState): GameState =
    File("save_data.sav").let {
        if (!it.exists()) {
            newGame()
        } else {
            json.decodeFromString<GameState>(it.readBytes().decompress())
        }
    }
