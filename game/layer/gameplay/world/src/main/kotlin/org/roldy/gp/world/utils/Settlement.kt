package org.roldy.gp.world.utils

import org.roldy.core.Vector2Int
import org.roldy.data.state.GameState
import org.roldy.data.state.SettlementState

object Settlement {

    fun find(
        state: GameState, coords: Vector2Int,
        onFound: (SettlementState) -> Unit
    ) {
        PlaceFinder.find(state.settlements, coords, onFound)
    }
}