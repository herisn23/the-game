package org.roldy.gameplay.world.generator.data

import org.roldy.core.Vector2Int
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.data.tile.TileData
import org.roldy.gameplay.world.generator.data.SettlementData

data class MineData(
    override val coords: Vector2Int,
    val harvestable: Harvestable,
    val settlementData: SettlementData?
) : TileData