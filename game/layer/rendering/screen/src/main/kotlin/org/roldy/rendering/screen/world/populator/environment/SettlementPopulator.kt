package org.roldy.rendering.screen.world.populator.environment

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.logger
import org.roldy.data.state.SettlementState
import org.roldy.rendering.environment.TileDecorationAtlas
import org.roldy.rendering.environment.TileDecorationNormal
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SettlementTileBehaviour
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator


class SettlementPopulator(
    override val map: WorldMap,
    val atlas: TileDecorationAtlas,
    val settlements: List<SettlementState>
) : AutoDisposableAdapter(), WorldChunkPopulator {
    val logger by logger()
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
            SettlementTileBehaviour.Data(
                position = position,
                coords = settle.coords,
                textureRegion = atlas.region<TileDecorationNormal> { castleFortified },
                borderTextureRegion = border,
                settlementData = settle,
                worldPosition = ::worldPosition,
                inBounds = map.mapBounds::isInBounds
            )
        }
    }
}