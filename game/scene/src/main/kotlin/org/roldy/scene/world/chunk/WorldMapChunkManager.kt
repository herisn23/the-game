package org.roldy.scene.world.chunk

import org.roldy.core.Vector2Int
import org.roldy.core.renderer.chunk.ChunkManager
import org.roldy.core.renderer.chunk.ChunkPopulator
import org.roldy.core.renderer.chunk.DrawableBuilder
import org.roldy.environment.MapObjectData
import org.roldy.map.WorldMapSize

class WorldMapChunkManager(
    private val tileSize: Int,
    private val mapSize: WorldMapSize,
    populator: ChunkPopulator<MapObjectData, WorldMapChunk>,
    drawableBuilder: DrawableBuilder<MapObjectData>
) : ChunkManager<MapObjectData, WorldMapChunk>(populator, drawableBuilder) {
    override val minCoords: Int = 0
    override val maxCoords: Int = mapSize.chunks - 1

    override val chunkSize: Float by lazy {
        ((mapSize.size / mapSize.chunks) * tileSize).toFloat()
    }

    override fun getChunk(coords: Vector2Int): WorldMapChunk =
        WorldMapChunk(coords, chunkSize, tileSize)

}