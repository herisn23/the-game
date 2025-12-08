package org.roldy.scene.world.populator

import com.badlogic.gdx.utils.Disposable
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposableList
import org.roldy.core.renderer.chunk.ChunkPopulator
import org.roldy.environment.MapObjectData
import org.roldy.map.WorldMap
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.pathfinding.Pathfinder
import org.roldy.scene.world.populator.environment.FoliagePopulator
import org.roldy.scene.world.populator.environment.RoadsPopulator
import org.roldy.scene.world.populator.environment.SettlementPopulator

class WorldMapPopulator(
    override val map: WorldMap,
    pathfinder: Pathfinder
) : AutoDisposableAdapter(), ChunkPopulator<MapObjectData, WorldMapChunk>, Disposable, WorldPopulator {
    val settlementPopulator = SettlementPopulator(map)
    val roadsPopulator = RoadsPopulator(map, pathfinder, settlementPopulator.settlements)
    val populators: List<WorldChunkPopulator> = disposableList(
        settlementPopulator,
        roadsPopulator,
        FoliagePopulator(map)
    )

    override fun populate(
        chunk: WorldMapChunk
    ): List<MapObjectData> =
        mutableListOf<MapObjectData>().apply {
            this += populators.flatMap { it.populate(chunk, this) }
        }
}