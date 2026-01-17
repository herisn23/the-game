package org.roldy.data.tile

import org.roldy.core.Vector2Int
import org.roldy.data.configuration.biome.BiomeData

data class FoliageTileData(
    override val coords: Vector2Int,
    val data: BiomeData.StaticSpawnData
) : TileData {
    override val walkCost: Float = data.walkCost
}