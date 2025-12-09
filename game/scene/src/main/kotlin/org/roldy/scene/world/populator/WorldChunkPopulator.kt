package org.roldy.scene.world.populator

import org.roldy.core.Vector2Int
import org.roldy.core.disposable.AutoDisposable
import org.roldy.environment.TileObject
import org.roldy.scene.world.chunk.WorldMapChunk

interface WorldChunkPopulator: AutoDisposable, WorldPopulator {

    fun populate(chunk: WorldMapChunk, existingObjects: List<TileObject.Data>): List<TileObject.Data>

    fun List<TileObject.Data>.find(coords: Vector2Int): List<TileObject.Data> =
        filter { it.coords == coords }
}