package org.roldy.core.stream.chunk

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import kotlin.math.floor

class ChunkDataManager(
    private val chunkSize: Float = 512f, //in future set chunkSize = tileSize × tilesPerChunk
    private val atlas: TextureAtlas
) {
    private val chunks = mutableMapOf<ChunkCoord, Chunk>()

    // Provide / generate chunk data on demand
    fun getOrCreateChunk(cx: Int, cy: Int): Chunk {
        val key = ChunkCoord(cx, cy)
        return chunks.getOrPut(key) {
            Chunk(key).apply {
                repeat(1) {
                    val x = cx * chunkSize + (Math.random() * chunkSize).toFloat()
                    val y = cy * chunkSize + (Math.random() * chunkSize).toFloat()
                    items += ChunkItemData(type = atlas.regions.random().name, x = x, y = y)
                }
            }
        }
    }

    fun worldToChunk(x: Float, y: Float): ChunkCoord {
        val cx = floor(x / chunkSize).toInt()
        val cy = floor(y / chunkSize).toInt()
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
