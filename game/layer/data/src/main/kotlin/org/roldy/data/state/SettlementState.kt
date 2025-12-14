package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.core.Vector2Int

@Serializable
data class SettlementState(
    val coords: Vector2Int,
    var ruler: RulerState,
    var mines: List<MineState>,
    var region: List<Vector2Int>,
    val texture: String
)