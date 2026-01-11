package org.roldy.rendering.screen.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.logger
import org.roldy.core.utils.get
import org.roldy.core.x
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.state.SettlementState
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SettlementTileBehaviour
import org.roldy.rendering.environment.item.SpriteTileBehaviour
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator
import org.roldy.rendering.tiles.Environment


class SettlementPopulator(
    override val map: WorldMap,
    atlas: TextureAtlas,
    val settlements: List<SettlementState>
) : AutoDisposableAdapter(), WorldChunkPopulator {
    val logger by logger()

    data class TextureConfig(
        val region: TextureRegion,
        val offset: Vector2
    )

    data class Config(
        val base: TextureConfig,
        val snow: TextureConfig
    )

    val settlementsConfig = listOf(
        Config(
            TextureConfig(
                atlas[Environment.castle_1_2],
                -341.4f x -128.9f
            ),
            TextureConfig(
                atlas[Environment.castle1_2_snow],
                -54.4f x 30.2f
            )
        ),
        Config(
            TextureConfig(
                atlas[Environment.castle_1_1],
                -355f x -150f
            ),
            TextureConfig(
                atlas[Environment.castle1_1_snow],
                -53f x 62.7f
            )
        ),
        Config(
            TextureConfig(
                atlas[Environment.castle_1_3],
                -128.8f x -66.7f
            ),
            TextureConfig(
                atlas[Environment.castle1_3_snow],
                -69.1f x 19.2f
            )
        )
    )

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.terrainData()
        val settlementsInChunk = settlements.filter { settlement ->
            data.contains(settlement.coords)
        }
        return settlementsInChunk.map { settle ->
            val terrain = data.getValue(settle.coords)
            val position = worldPosition(settle.coords)
            val config = settlementsConfig[settle.type]
            SettlementTileBehaviour.Data(
                base = SpriteTileBehaviour.Data(
                    position = position,
                    coords = settle.coords,
                    offset = config.base.offset,
                    textureRegion = config.base.region,
                ),
                snow = SpriteTileBehaviour.Data(
                    position = position,
                    coords = settle.coords,
                    offset = config.snow.offset,
                    textureRegion = config.snow.region,
                ),
                hasSnow = terrain.terrain.biome.data.type == BiomeType.Cold,
                worldPosition = ::worldPosition,
                state = settle
            )
        }
    }
}