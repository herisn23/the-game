package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.data.map.MapData

@Serializable
data class GameState(
    val mapData: MapData,
    var time: Float = 0f,
    val settlements: List<SettlementState>,
    val mines: List<MineState>, //free mines, it means outside of settlement radius
    val player: PlayerState
)