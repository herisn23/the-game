package org.roldy.rendering.screen.world.populator

import org.roldy.core.Vector2Int
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.map.MapTerrainData
import org.roldy.rendering.screen.world.chunk.WorldMapChunk

interface WorldChunkPopulator : AutoDisposable, WorldPopulator {

    fun populate(chunk: WorldMapChunk, existingObjects: List<TileObject.Data>): List<TileObject.Data>

    fun List<TileObject.Data>.find(coords: Vector2Int): List<TileObject.Data> =
        filter { it.coords == coords }

    fun List<TileObject.Data>.hasData(coords: Vector2Int): Boolean =
        find(coords).isNotEmpty()

    fun WorldMapChunk.availableTerrainData(existingObjects: List<TileObject.Data>): Map<Vector2Int, MapTerrainData> =
        terrainData().filter {
            !existingObjects.hasData(it.key)
        }
}