package org.roldy.data.tile.mountain

import org.roldy.core.Vector2Int
import org.roldy.data.tile.TileData

data class MountainData(
    override val coords: Vector2Int
) : TileData {
    override val walkCost: Float = 0f
}
