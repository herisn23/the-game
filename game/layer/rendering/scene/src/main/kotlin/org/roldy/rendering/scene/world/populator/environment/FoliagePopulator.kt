package org.roldy.rendering.scene.world.populator.environment

import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.scene.world.chunk.WorldMapChunk
import org.roldy.rendering.scene.world.populator.WorldChunkPopulator

class FoliagePopulator(override val map: WorldMap) : AutoDisposableAdapter(), WorldChunkPopulator {
    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        return emptyList()
    }
}