package org.roldy.core.stream

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle

class ChunkManager(
    private val chunkSize: Float = 256f,
    private val atlas: TextureAtlas
) {
    private val chunks = mutableMapOf<ChunkCoord, Chunk>()

    // Provide / generate chunk data on demand
    fun getOrCreateChunk(cx: Int, cy: Int): Chunk {
        val key = ChunkCoord(cx, cy)
        return chunks.getOrPut(key) {
            val chunk = Chunk(key)
            // Generate or load items for this chunk here (procedural or from file)
            // Example: add a few trees
            repeat(10) {
                val x = cx * chunkSize + (Math.random() * chunkSize).toFloat()
                val y = cy * chunkSize + (Math.random() * chunkSize).toFloat()
                chunk.items += MapItemData(type = atlas.regions.random().name, x = x, y = y)
            }
            chunk
        }
    }

    fun worldToChunk(x: Float, y: Float): ChunkCoord {
        val cx = kotlin.math.floor(x / chunkSize).toInt()
        val cy = kotlin.math.floor(y / chunkSize).toInt()
        return ChunkCoord(cx, cy)
    }

    fun chunksInView(view: Rectangle): List<Chunk> {
        val min = worldToChunk(view.x, view.y)
        val max = worldToChunk(view.x + view.width, view.y + view.height)
        val list = mutableListOf<Chunk>()
        for (cy in min.cy..max.cy) {
            for (cx in min.cx..max.cx) {
                list += getOrCreateChunk(cx, cy)
            }
        }
        return list
    }
}
