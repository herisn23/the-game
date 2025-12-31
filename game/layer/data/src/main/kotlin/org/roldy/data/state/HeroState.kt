package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.core.Vector2Int
import org.roldy.core.x
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class HeroState(
    var harvestingSpeed: Duration = 3.seconds,
    var inventorySize: Int = 5,
    var speed: Float = 1f,
    var coords: Vector2Int = 0 x 0,
    val inventory: InventoryState = InventoryState(),
)