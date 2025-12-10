package org.roldy.gameplay.world

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.x
import org.roldy.data.map.MapData
import org.roldy.rendering.map.TileData
import org.roldy.rendering.scene.world.distance
import org.roldy.rendering.scene.world.populator.environment.SettlementData
import kotlin.random.Random

object SettlementGenerator {
    val logger by logger()
    fun generate(
        terrainData: Map<Vector2Int, TileData>,
        mapData: MapData
    ): List<SettlementData> {
        val mapSize = mapData.size
        val seed = mapData.seed
        val count = mapSize.settlements
        val settlementRng = Random(seed + 1)
        val settlements = mutableListOf<SettlementData>()
        val attempts = count * 10 // Try multiple times to find good spots

        repeat(attempts) {
            if (settlements.size >= count) return@repeat

            val x = settlementRng.nextInt(mapSize.width)
            val y = settlementRng.nextInt(mapSize.height)
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