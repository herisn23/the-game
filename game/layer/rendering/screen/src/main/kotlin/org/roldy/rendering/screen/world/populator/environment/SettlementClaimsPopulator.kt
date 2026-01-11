package org.roldy.rendering.screen.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.logger
import org.roldy.core.utils.get
import org.roldy.data.state.SettlementState
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SettlementClaimsTileBehaviour
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator


class SettlementClaimsPopulator(
    override val map: WorldMap,
    val outlineAtlas: TextureAtlas,
    val settlements: List<SettlementState>
) : AutoDisposableAdapter(), WorldChunkPopulator {
    val logger by logger()

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.terrainData()
        val settlementsInChunk = settlements.filter { settlement ->
            data.contains(settlement.coords)
        }
        val region = outlineAtlas["outline_111111"]
        return settlementsInChunk.flatMap { settle ->
            (settle.claims + settle.reserved).map { coords ->
                val position = worldPosition(coords)
                SettlementClaimsTileBehaviour.Data(
                    region = region,
                    worldPosition = ::worldPosition,
                    position = position,
                    coords = coords,
                    color = settle.ruler.color
                )
            }
        }
    }
}