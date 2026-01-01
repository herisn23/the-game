package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.core.Vector2Int
import org.roldy.data.mine.harvestable.Harvestable
import kotlin.time.Duration

@Serializable
data class HarvestableState(
    val coords: Vector2Int,
    val harvestable: Harvestable,
    val refreshing: RefreshingState,
    var harvested: Int = 0,//harvesting progress until collected
    var currentHarvestingProgress: Duration = Duration.ZERO,
    val settlement: Int?
)