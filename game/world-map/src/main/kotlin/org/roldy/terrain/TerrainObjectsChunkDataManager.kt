package org.roldy.terrain

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.stream.chunk.Chunk
import org.roldy.core.stream.chunk.ChunkCoord
import org.roldy.core.stream.chunk.ChunkDataManager
import org.roldy.core.stream.chunk.ChunkItemData
import org.roldy.terrain.biome.Terrain

class TerrainObjectsChunkDataManager(
    chunkSize: Float,
    private val terrainData: Map<Pair<Int, Int>, Terrain>,
    private val atlas: TextureAtlas,
) : ChunkDataManager(chunkSize) {
    override fun getChunk(coords: ChunkCoord): Chunk =
        Chunk(coords).apply {
            repeat(1) {
                val x = coords.cx * chunkSize + (Math.random() * chunkSize).toFloat()
                val y = coords.cy * chunkSize + (Math.random() * chunkSize).toFloat()
                items += ChunkItemData(type = atlas.regions.random().name, x = x, y = y)
            }
        }

}