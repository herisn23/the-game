package org.roldy.data.state

import kotlinx.serialization.Serializable
import org.roldy.core.Vector2Int
import org.roldy.data.mine.harvestable.Harvestable

@Serializable
data class MineState(
    val coords: Vector2Int,
    val harvestable: Harvestable,
    var max: Int,
    var current: Int
)