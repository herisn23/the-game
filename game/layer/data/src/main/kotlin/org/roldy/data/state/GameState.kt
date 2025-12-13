package org.roldy.data.state

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val settlements: List<SettlementState>,
    val mines: List<MineState>, //free mines, it means outside of settlement radius
    val player: PlayerState
)