package org.roldy.rendering.screen.world.populator.environment

import org.roldy.core.asset.AtlasLoader
import org.roldy.core.logger
import org.roldy.data.state.MineState
import org.roldy.data.tile.mine.MineData
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SpriteTileObject
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator

class MinesPopulator(
    override val map: WorldMap,
    val mines: List<MineState>
) : AutoDisposableAdapter(), WorldChunkPopulator {

    val atlas = AtlasLoader.mines.disposable()

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.terrainData()
        val minesInChunk = mines.filter { settlement ->
            data.contains(settlement.coords)
        }
        return minesInChunk.map { mine ->
            val position = worldPosition(mine.coords)
            logger.debug { "Loading ${mine.coords} in chunk ${chunk.coords}" }
            SpriteTileObject.Data(
                position = position, coords = mine.coords,
                textureRegion = atlas.findRegion(mine.harvestable.mineType.texture),
            )
        }
    }

}