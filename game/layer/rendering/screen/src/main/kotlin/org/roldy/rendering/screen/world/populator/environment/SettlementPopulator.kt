package org.roldy.rendering.screen.world.populator.environment

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.logger
import org.roldy.data.state.SettlementState
import org.roldy.data.tile.settlement.SettlementData
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SettlementTileObject
import org.roldy.rendering.environment.item.SpriteTileObject
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator


class SettlementPopulator(
    override val map: WorldMap,
    val settlements: List<SettlementState>
) : AutoDisposableAdapter(), WorldChunkPopulator {
    val logger by logger()
    val atlas = AtlasLoader.settlements.disposable()
    val border = Texture("HexTileHighlighter.png").disposable().let(::TextureRegion)

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.terrainData()
        val settlementsInChunk = settlements.filter { settlement ->
            data.contains(settlement.coords)
        }
        return settlementsInChunk.map { settle ->
            val position = worldPosition(settle.coords)
            logger.debug { "Loading ${settle.coords} in chunk ${chunk.coords}" }
            SettlementTileObject.Data(
                position = position,
                coords = settle.coords,
                textureRegion = atlas.findRegion("hexDirtCastle00_blue"),
                borderTextureRegion = border,
                settlementData = settle,
                worldPosition = ::worldPosition,
                inBounds = map.mapBounds::isInBounds
            )
        }
    }
}