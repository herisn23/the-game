package org.roldy.data.tile

import org.roldy.core.Vector2Int
import org.roldy.data.mine.harvestable.Harvestable

data class HarvestableTileData(
    override val coords: Vector2Int,
    val harvestable: Harvestable,
    val settlementData: SettlementTileData?
) : TileData