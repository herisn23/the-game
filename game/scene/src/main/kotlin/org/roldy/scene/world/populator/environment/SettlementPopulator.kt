package org.roldy.scene.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.Vector2Int
import org.roldy.core.asset.loadAsset
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.logger
import org.roldy.core.x
import org.roldy.environment.TileObject
import org.roldy.environment.item.SpriteTileObject
import org.roldy.map.WorldMap
import org.roldy.map.WorldMapSize
import org.roldy.scene.distance
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.populator.WorldChunkPopulator
import org.roldy.terrain.TileData
import kotlin.random.Random

data class SettlementData(
    val coords: Vector2Int,
    val name: String
)

class SettlementPopulator(override val map: WorldMap) : AutoDisposableAdapter(), WorldChunkPopulator {
    val logger by logger()
    val atlas by disposable { TextureAtlas(loadAsset("environment/Settlements.atlas")) }
    val settlements = generate(map.terrainData, map.mapSize, map.seed)

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

    private fun generate(
        terrainData: Map<Vector2Int, TileData>,
        mapSize: WorldMapSize,
        seed: Long
    ): List<SettlementData> {
        val count = mapSize.settlements
        val settlementRng = Random(seed + 1)
        val settlements = mutableListOf<SettlementData>()
        val attempts = count * 10 // Try multiple times to find good spots

        repeat(attempts) {
            if (settlements.size >= count) return@repeat

            val x = settlementRng.nextInt(mapSize.size)
            val y = settlementRng.nextInt(mapSize.size)
            val coords = x x y
            val tile = terrainData.getValue(coords)

            // Check if location is suitable
            val isSuitable = tile.noiseData.elevation in 0.1f..0.65f &&  // Not too high/low
//                    tile.moisture > 0.3f &&           // Not desert
                    tile.noiseData.temperature in 0.3f..0.7f &&        // Temperate
                    settlements.none {
                        distance(x, y, it.coords.x, it.coords.y) < 5  // Min distance from others
                    }

            if (isSuitable) {
                logger.debug { "Generated $coords" }
                settlements.add(SettlementData(coords, "Settlement${settlements.size + 1}"))
            }
        }
        return settlements
    }
}