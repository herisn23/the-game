package org.roldy.map

import org.roldy.core.Vector2Int
import org.roldy.core.renderer.chunk.ChunkDataManager
import org.roldy.core.renderer.chunk.ChunkPopulator

class WorldMapChunkDataManager(
    private val tileSize: Int,
    private val mapSize: WorldMapSize,
    populator: ChunkPopulator<WorldMapChunk>
) : ChunkDataManager<WorldMapChunk>(populator) {
    override val minCoords: Int = 0
    override val maxCoords: Int = mapSize.chunks - 1

    override val chunkSize: Float by lazy {
        ((mapSize.size / mapSize.chunks) * tileSize).toFloat()
    }

    override fun getChunk(coords: Vector2Int): WorldMapChunk =
        WorldMapChunk(coords, chunkSize, tileSize)

}