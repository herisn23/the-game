package org.roldy.rendering.screen.world.populator.environment

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.plus
import org.roldy.data.state.SettlementState
import org.roldy.data.tile.HexEdgeDirection
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SettlementClaimsTileBehaviour
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator


class SettlementClaimsPopulator(
    override val map: WorldMap,
    val outlineAtlas: TextureAtlas,
    val emptyHex: Texture,
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
        return settlementsInChunk.flatMap { settle ->
            val allClaims = settle.claims + settle.reserved

            allClaims.map { coords ->
                val bitmask = resolveBitmasks(allClaims, map)
                val position = worldPosition(coords)
                val name = "outline_${bitmask[coords]}"
                SettlementClaimsTileBehaviour.Data(
                    borderRegion = outlineAtlas.findRegion(name),
                    backgroundRegion = TextureRegion(emptyHex),
                    worldPosition = ::worldPosition,
                    position = position,
                    coords = coords,
                    color = settle.ruler.color
                )
            }
        }
    }

    fun resolveBitmasks(
        claims: List<Vector2Int>,
        map: WorldMap
    ): Map<Vector2Int, String> {
        val claimSet = claims.toSet()

        return claims.associateWith { coord ->
            val directionOrder = listOf(
                HexEdgeDirection.NORTHWEST,
                HexEdgeDirection.NORTHEAST,
                HexEdgeDirection.EAST,
                HexEdgeDirection.SOUTHEAST,
                HexEdgeDirection.SOUTHWEST,
                HexEdgeDirection.WEST
            )

            directionOrder.joinToString("") { direction ->
                val neighbor = coord + direction.getOffset(!map.isStaggered(coord))
                if (neighbor in claimSet) "0" else "1"
            }
        }
    }
}