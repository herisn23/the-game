package org.roldy.data.tile.mine

import org.roldy.core.Vector2Int
import org.roldy.data.tile.TileData
import org.roldy.data.tile.mine.harvestable.Harvestable
import org.roldy.data.tile.settlement.SettlementData

data class MineData(
    override val coords: Vector2Int,
    val harvestable: Harvestable,
    val settlementData: SettlementData?
) : TileData