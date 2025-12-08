package org.roldy.scene.world.populator

import org.roldy.core.Vector2Int
import org.roldy.core.disposable.AutoDisposable
import org.roldy.environment.MapObjectData
import org.roldy.scene.world.chunk.WorldMapChunk

interface WorldChunkPopulator: AutoDisposable, WorldPopulator {

    fun populate(chunk: WorldMapChunk, existingObjects: List<MapObjectData>): List<MapObjectData>

    fun List<MapObjectData>.find(coords: Vector2Int): List<MapObjectData> =
        filter { it.coords == coords }
}