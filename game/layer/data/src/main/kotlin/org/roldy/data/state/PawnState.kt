package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.core.Vector2Int
import org.roldy.core.x
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class PawnState(
    var defaultTileSpeed: Float = 10000f,
    var mineSpeed: Duration = 3.seconds,
    var speed: Float = 0.5f,
    var coords: Vector2Int = 0 x 0
)