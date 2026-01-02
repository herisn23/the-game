package org.roldy.rendering.screen.world.populator.environment

import org.roldy.core.asset.AtlasLoader
import org.roldy.data.state.HarvestableState
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.HarvestableTileObject
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator

class HarvestablePopulator(
    override val map: WorldMap,
    val harvestable: List<HarvestableState>
) : AutoDisposableAdapter(), WorldChunkPopulator {

    val atlas = AtlasLoader.craftingIcons.disposable()

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.terrainData()
        val harvestableInChunk = harvestable.filter { harvest ->
            data.contains(harvest.coords)
        }
        return harvestableInChunk.map { harvestable ->
            val position = worldPosition(harvestable.coords)
            HarvestableTileObject.Data(
                position = position,
                coords = harvestable.coords,
                textureRegion = atlas.findRegion(harvestable.harvestable.key),
                state = harvestable
            )
        }
    }

}