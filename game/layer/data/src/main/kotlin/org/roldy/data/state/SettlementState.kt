package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.core.Vector2Int

@Serializable
data class SettlementState(
    val id: Int,
    override val coords: Vector2Int,
    var ruler: RulerState,
    var claims: List<Vector2Int>,
    val type: Int = 0
) : Positioned