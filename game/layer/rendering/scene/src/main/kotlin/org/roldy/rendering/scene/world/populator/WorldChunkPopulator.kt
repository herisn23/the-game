package org.roldy.rendering.scene.world.populator

import org.roldy.core.Vector2Int
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.scene.world.chunk.WorldMapChunk

interface WorldChunkPopulator: AutoDisposable, WorldPopulator {

    fun populate(chunk: WorldMapChunk, existingObjects: List<TileObject.Data>): List<TileObject.Data>

    fun List<TileObject.Data>.find(coords: Vector2Int): List<TileObject.Data> =
        filter { it.coords == coords }
}