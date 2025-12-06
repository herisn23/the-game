package org.roldy.scene.world.populator

import org.roldy.core.Vector2Int
import org.roldy.core.x
import org.roldy.map.WorldMapSize
import org.roldy.scene.distance
import org.roldy.terrain.TileData
import kotlin.random.Random

data class SettlementData(
    val coords: Vector2Int,
    val name: String
)

object SettlementsGenerator {

    fun generate(
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
            val isSuitable = tile.elevation in 0.1f..0.65f &&  // Not too high/low
//                    tile.moisture > 0.3f &&           // Not desert
                    tile.temperature in 0.3f..0.7f &&        // Temperate
                    settlements.none {
                        distance(x, y, it.coords.x, it.coords.y) < 15  // Min distance from others
                    }

            if (isSuitable) {
                settlements.add(SettlementData(coords, "Settlement${settlements.size + 1}"))
            }
        }
        return settlements
    }
}