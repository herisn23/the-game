package org.roldy.map

import org.roldy.core.Vector2Int
import org.roldy.core.renderer.chunk.Chunk
import org.roldy.core.repeat
import org.roldy.core.x

class WorldMapChunk(coords: Vector2Int, size: Float, val tileSize: Int) : Chunk(coords, size) {
    val tilesCoords: List<Vector2Int> =
        mutableListOf<Vector2Int>().apply {
            val tilesPerChunk = (size / tileSize).toInt()
            repeat(
                coords.x*tilesPerChunk..<coords.x*tilesPerChunk + tilesPerChunk,
                coords.y*tilesPerChunk..<coords.y*tilesPerChunk + tilesPerChunk
            ) { x, y ->
                this += x x y
            }
        }

}