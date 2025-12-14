package org.roldy.data.tile

import org.roldy.core.Vector2Int

data class MountainTileData(
    override val coords: Vector2Int
) : TileData {
    override val walkCost: Float = 0f
}