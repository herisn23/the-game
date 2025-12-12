package org.roldy.rendering.screen.world.chunk

import org.roldy.core.Vector2Int
import org.roldy.core.repeat
import org.roldy.core.x
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.chunk.Chunk


class WorldMapChunk(
    coords: Vector2Int,
    chunkWidth: Float,
    chunkHeight: Float,
    val tilesX: Int,
    val tilesY: Int
) : Chunk<TileObject.Data>(coords, chunkWidth, chunkHeight) {
    val tilesCoords: List<Vector2Int> =
        mutableListOf<Vector2Int>().apply {

            repeat(
                coords.x * tilesX..<coords.x * tilesX + tilesX,
                coords.y * tilesY..<coords.y * tilesY + tilesY
            ) { x, y ->
                this += x x y
            }
        }

}