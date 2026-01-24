package org.roldy.core.pathwalker

import org.roldy.core.Vector2Int

interface TileWalker {
    var coords: Vector2Int
    val speed: Float

    fun onPathEnd(coords: Vector2Int)
    fun onTileEnter(coords: Vector2Int)
    fun onTileLeave(coords: Vector2Int)
    fun nextTile(coords: Vector2Int)
}