package org.roldy.scene.world.chunk

import org.roldy.core.Vector2Int
import org.roldy.core.renderer.chunk.ChunkManager
import org.roldy.core.renderer.chunk.ChunkPopulator
import org.roldy.environment.MapObject
import org.roldy.environment.MapObjectData
import org.roldy.map.WorldMap

class WorldMapChunkManager(
    private val map: WorldMap,
    populator: ChunkPopulator<MapObjectData, WorldMapChunk>
) : ChunkManager<MapObjectData, WorldMapChunk>(populator, { MapObject() }) {
    override val minCoords: Int = 0
    override val maxCoords: Int = map.mapSize.chunks - 1

    override val chunkWidth: Float by lazy {
        (((map.mapSize.size * map.tileSize) / map.mapSize.chunks)).toFloat()
    }

    override val chunkHeight: Float by lazy {
        (((map.mapSize.size * map.tileSize * map.yCorrection) / map.mapSize.chunks)).toFloat()
    }
    val tilesX = (chunkWidth / map.tileSize).toInt()
    val tilesY = (chunkHeight / map.tileSize * (1 / map.yCorrection)).toInt()

    override fun getChunk(coords: Vector2Int): WorldMapChunk =
        WorldMapChunk(coords, chunkWidth, chunkHeight, tilesX, tilesY)


}