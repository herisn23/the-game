package org.roldy.core.stream.chunk

import com.badlogic.gdx.math.Rectangle
import kotlin.math.floor

abstract class ChunkDataManager(
    val chunkSize: Float, //in future set chunkSize = tileSize × tilesPerChunk
) {
    private val chunks = mutableMapOf<ChunkCoord, Chunk>()

    abstract fun getChunk(coords: ChunkCoord): Chunk

    // Provide / generate chunk data on demand
    internal fun getOrCreateChunk(coords: ChunkCoord): Chunk {
        return chunks.getOrPut(coords) {
            getChunk(coords)
        }
    }

    internal fun chunksInView(view: Rectangle): List<Chunk> {
        val min = worldToChunk(view.x, view.y)
        val max = worldToChunk(view.x + view.width, view.y + view.height)
        val list = mutableListOf<Chunk>()
        for (cy in min.cy..max.cy) {
            for (cx in min.cx..max.cx) {
                list += getOrCreateChunk(ChunkCoord(cx, cy))
            }
        }
        return list
    }

    private fun worldToChunk(x: Float, y: Float): ChunkCoord {
        val cx = floor(x / chunkSize).toInt()
        val cy = floor(y / chunkSize).toInt()
        return ChunkCoord(cx, cy)
    }
}
