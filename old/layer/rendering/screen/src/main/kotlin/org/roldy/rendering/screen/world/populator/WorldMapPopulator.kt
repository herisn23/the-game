package org.roldy.rendering.screen.world.populator

import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.chunk.ChunkPopulator
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk

class WorldMapPopulator(
    override val map: WorldMap,
    val populators: List<WorldChunkPopulator>,
    val persistentItems: List<Layered>
) : ChunkPopulator<TileObject.Data, WorldMapChunk>, WorldPopulator {

    override fun populate(
        chunk: WorldMapChunk
    ): List<TileObject.Data> =
        mutableListOf<TileObject.Data>().apply {
            this += populators.flatMap { it.populate(chunk, this) }
        }

    override fun createPersistentItems(): List<Layered> =
        persistentItems
}