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
    val tilesX: Float,
    val tilesY: Float
) : Chunk<TileObject.Data>(coords, chunkWidth, chunkHeight) {
    // Calculate actual tile ranges using floor/ceil
    val startTileX = (coords.x * tilesX).toInt()
    val endTileX = ((coords.x + 1) * tilesX).toInt()

    val startTileY = (coords.y * tilesY).toInt()  // Should give 160 for chunk 12
    val endTileY = ((coords.y + 1) * tilesY).toInt()


    val tilesCoords: List<Vector2Int> =
        mutableListOf<Vector2Int>().apply {

            repeat(
                startTileX..<endTileX,
                startTileY..<endTileY
            ) { x, y ->
                this += x x y
            }
        }

    override fun toString(): String {
        return """
            =====
            [$coords]
                ${startTileX}..<${endTileX}
                ${startTileY}..<${endTileY}
            ====
        """.trimIndent()
    }
}