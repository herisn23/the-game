package org.roldy.data.tile

import org.roldy.core.Vector2Int
import org.roldy.core.pathwalker.PathWalker

data class RoadTileData(
    val node: PathWalker.Node,
    val bitmask: String
) : TileData {
    override val walkCost: Float = 2f
    override val coords: Vector2Int = node.coords
}