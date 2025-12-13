package org.roldy.data.tile.road

import org.roldy.core.Vector2Int
import org.roldy.core.pathwalker.PathWalker
import org.roldy.data.tile.TileData

data class RoadData(
    val node: PathWalker.Node,
    val bitmask: String
) : TileData {
    override val walkCost: Float = 2f
    override val coords: Vector2Int = node.coords
}
