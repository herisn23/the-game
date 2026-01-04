package org.roldy.data.tile

import org.roldy.core.Vector2Int
import org.roldy.data.configuration.biome.BiomeData

data class MountainTileData(
    override val coords: Vector2Int,
    val data: BiomeData.SpawnData
) : TileData {
    override val walkCost: Float = -1f
}