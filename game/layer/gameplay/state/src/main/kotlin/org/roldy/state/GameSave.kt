package org.roldy.state

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.roldy.core.coroutines.async
import org.roldy.data.GameState
import java.io.File

fun GameState.save() {
    async {
        val str = Json.encodeToString(this)
        File("save_data.json").writeText(str)
    }
}