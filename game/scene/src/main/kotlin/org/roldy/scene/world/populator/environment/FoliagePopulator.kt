package org.roldy.scene.world.populator.environment

import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.environment.TileObject
import org.roldy.map.WorldMap
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.populator.WorldChunkPopulator

class FoliagePopulator(
    override val map: WorldMap
): AutoDisposableAdapter(), WorldChunkPopulator {
    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        return emptyList()
    }
}