package org.roldy.rendering.scene.world.populator

import com.badlogic.gdx.utils.Disposable
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.chunk.ChunkPopulator
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.scene.world.chunk.WorldMapChunk

class WorldMapPopulator(
    override val map: WorldMap,
    val populators: List<WorldChunkPopulator>
) : AutoDisposableAdapter(), ChunkPopulator<TileObject.Data, WorldMapChunk>, Disposable, WorldPopulator {

    override fun populate(
        chunk: WorldMapChunk
    ): List<TileObject.Data> =
        mutableListOf<TileObject.Data>().apply {
            this += populators.flatMap { it.populate(chunk, this) }
        }
}