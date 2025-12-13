package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.core.Vector2Int
import org.roldy.core.x

@Serializable
data class PawnState(
    var defaultTileSpeed: Float = 10000f,
    var speed: Float = 0.5f,
    var coords: Vector2Int = 0 x 0
)