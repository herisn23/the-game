package org.roldy.rendering.screen.world.chunk

import org.roldy.core.Vector2Int
import org.roldy.data.tile.TileData
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.chunk.ChunkManager
import org.roldy.rendering.g2d.chunk.ChunkPopulator
import org.roldy.rendering.map.WorldMap


class WorldMapChunkManager(
    private val map: WorldMap,
    populator: ChunkPopulator<TileObject.Data, WorldMapChunk>
) : ChunkManager<TileObject.Data, WorldMapChunk>(populator, { TileObject() }) {
    override val minCoords: Int = 0
    override val maxCoords: Int = map.data.size.chunks - 1

    override val chunkWidth: Float by lazy {
        (((map.data.size.size * map.data.tileSize) / map.data.size.chunks)).toFloat()
    }

    override val chunkHeight: Float by lazy {
        (((map.data.size.size * map.data.tileSize * map.yCorrection) / map.data.size.chunks))
    }
    val tilesX = (chunkWidth / map.data.tileSize).toInt()
    val tilesY = (chunkHeight / map.data.tileSize * (1 / map.yCorrection)).toInt()

    override fun getChunk(coords: Vector2Int): WorldMapChunk =
        WorldMapChunk(coords, chunkWidth, chunkHeight, tilesX, tilesY)

    fun tileData(coords: Vector2Int): List<TileData> =
        visibleChunks.flatMap { it.allObjects.flatMap { o -> o.data.tileData } }.filter {
            it.coords == coords
        }
}