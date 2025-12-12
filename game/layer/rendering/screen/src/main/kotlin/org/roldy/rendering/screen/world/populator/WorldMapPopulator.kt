package org.roldy.rendering.screen.world.populator

import com.badlogic.gdx.utils.Disposable
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.chunk.ChunkPopulator
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk

class WorldMapPopulator(
    override val map: WorldMap,
    populators: List<WorldChunkPopulator>,
    val persistentItems: List<Layered>
) : AutoDisposableAdapter(), ChunkPopulator<TileObject.Data, WorldMapChunk>, Disposable, WorldPopulator {

    private val populators = populators.map { it.disposable() }
    override fun populate(
        chunk: WorldMapChunk
    ): List<TileObject.Data> =
        mutableListOf<TileObject.Data>().apply {
            this += populators.flatMap { it.populate(chunk, this) }
        }

    override fun createPersistentItems(): List<Layered> =
        persistentItems
}