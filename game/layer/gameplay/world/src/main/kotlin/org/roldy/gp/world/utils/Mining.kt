package org.roldy.gp.world.utils

import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.async
import org.roldy.data.state.GameState
import org.roldy.data.state.MineState

object Mining {

    fun findMine(
        state: GameState, coords: Vector2Int,
        onMineFound: (MineState) -> Unit
    ) {
        async { main ->
            val foundMine = state.mines.find { mine -> mine.coords == coords }
            main {
                foundMine?.let {
                    onMineFound(foundMine)
                }
            }
        }
    }

    fun startMine(mine: MineState) {

    }
}