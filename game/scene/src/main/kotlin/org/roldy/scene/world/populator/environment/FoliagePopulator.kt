package org.roldy.scene.world.populator.environment

import org.roldy.environment.MapObjectData
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.populator.WorldChunkPopulator

class FoliagePopulator(
    val seed:Long
): WorldChunkPopulator {
    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<MapObjectData>
    ): List<MapObjectData> {
        return emptyList()
    }
}