package org.roldy.scene.world.populator

import com.badlogic.gdx.utils.Disposable
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposableList
import org.roldy.core.renderer.chunk.ChunkPopulator
import org.roldy.environment.TileObject
import org.roldy.map.WorldMap
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.pathfinding.Pathfinder
import org.roldy.scene.world.populator.environment.FoliagePopulator
import org.roldy.scene.world.populator.environment.RoadsPopulator
import org.roldy.scene.world.populator.environment.SettlementPopulator

class WorldMapPopulator(
    override val map: WorldMap,
    pathfinder: Pathfinder
) : AutoDisposableAdapter(), ChunkPopulator<TileObject.Data, WorldMapChunk>, Disposable, WorldPopulator {
    val settlementPopulator = SettlementPopulator(map)
    val roadsPopulator = RoadsPopulator(map, pathfinder, settlementPopulator.settlements)
    val populators: List<WorldChunkPopulator> = disposableList(
        settlementPopulator,
        roadsPopulator,
        FoliagePopulator(map)
    )

    override fun populate(
        chunk: WorldMapChunk
    ): List<TileObject.Data> =
        mutableListOf<TileObject.Data>().apply {
            this += populators.flatMap { it.populate(chunk, this) }
        }
}