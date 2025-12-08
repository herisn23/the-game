package org.roldy.scene.world.populator.environment

import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.environment.MapObjectData
import org.roldy.map.WorldMap
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.populator.WorldChunkPopulator

class FoliagePopulator(
    override val worldMap: WorldMap
): AutoDisposableAdapter(), WorldChunkPopulator {
    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<MapObjectData>
    ): List<MapObjectData> {
        return emptyList()
    }
}