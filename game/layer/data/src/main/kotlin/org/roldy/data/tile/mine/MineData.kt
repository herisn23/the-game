package org.roldy.data.tile.mine

import org.roldy.core.Vector2Int
import org.roldy.data.tile.TileData
import org.roldy.data.tile.mine.harvestable.Harvestable

data class MineData(
    override val coords: Vector2Int,
    val name: String,
    val harvestable: Harvestable
): TileData