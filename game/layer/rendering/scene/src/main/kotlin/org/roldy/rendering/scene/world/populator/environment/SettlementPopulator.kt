package org.roldy.rendering.scene.world.populator.environment

import org.roldy.core.Vector2Int
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.logger
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SpriteTileObject
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.scene.world.chunk.WorldMapChunk
import org.roldy.rendering.scene.world.populator.WorldChunkPopulator

data class SettlementData(
    val coords: Vector2Int,
    val name: String
)

class SettlementPopulator(
    override val map: WorldMap,
    val settlements: List<SettlementData>
) : AutoDisposableAdapter(), WorldChunkPopulator {
    val logger by logger()
    val atlas = AtlasLoader.settlements.disposable()

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.data()
        val settlementsInChunk = settlements.filter { settlement ->
            data.contains(settlement.coords)
        }
        return settlementsInChunk.map { settle ->
            val position = worldPosition(settle.coords)
            logger.debug { "Loading ${settle.coords} in chunk ${chunk.coords}" }
            SpriteTileObject.Data(
                name = settle.name, position = position, coords = settle.coords,
                textureRegion = atlas.findRegion("hexDirtCastle00_blue")
            )
        }
    }
}