package org.roldy.map

import org.roldy.core.stream.chunk.Chunk
import org.roldy.core.stream.chunk.ChunkCoord
import org.roldy.core.stream.chunk.ChunkDataManager
import org.roldy.core.stream.chunk.ChunkItemData
import org.roldy.terrain.biome.Terrain

fun interface WorldMapChunkItemDataProcessor {
    fun get(coords: ChunkCoord, chunkSize:Float): List<ChunkItemData>
}

class WorldMapChunkDataManager(
    chunkSize: Float,
    private val terrainData: Map<Pair<Int, Int>, Terrain>,
    private val itemData: WorldMapChunkItemDataProcessor
) : ChunkDataManager(chunkSize) {

    override fun hasChunk(coords: ChunkCoord): Boolean {
//        logger.debug { "$coords" }
        return false
    }

    override fun getChunk(coords: ChunkCoord): Chunk =
        Chunk(coords).apply {
            items.addAll(itemData.get(coords, chunkSize))
        }

}