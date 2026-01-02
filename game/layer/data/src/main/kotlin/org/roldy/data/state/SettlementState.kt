package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.core.Vector2Int

@Serializable
data class SettlementState(
    val id: Int,
    val coords: Vector2Int,
    var ruler: RulerState,
    var region: List<Vector2Int>
)