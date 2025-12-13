package org.roldy.gameplay.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.x
import org.roldy.data.map.MapData
import org.roldy.rendering.map.MapTerrainData
import org.roldy.rendering.screen.world.distance
import org.roldy.rendering.screen.world.populator.environment.MineData
import org.roldy.rendering.screen.world.populator.environment.SettlementData
import kotlin.random.Random

class MinesGenerator(
    val terrainData: Map<Vector2Int, MapTerrainData>,
    val mapData: MapData,
    val settlementData: List<SettlementData>
) {

    val logger by logger()
    fun generate(): List<MineData> {
        val mapSize = mapData.size
        val seed = mapData.seed
        val count = mapSize.settlements
        val mineRng = Random(seed + 1)
        val mines = mutableListOf<MineData>()
        val attempts = count * 10 // Try multiple times to find good spots

        repeat(attempts) {
            if (mines.size >= count) return@repeat

            val x = mineRng.nextInt(mapSize.width)
            val y = mineRng.nextInt(mapSize.height)
            val coords = x x y
            val tile = terrainData.getValue(coords)

            // Check if location is suitable
            val isSuitable =
                    tile.noiseData.elevation < 0.7f &&
                    mines.none {
                        distance(x, y, it.coords.x, it.coords.y) < 5  // Min distance from others
                    }

            if (isSuitable) {
                logger.debug { "Generated mine at $coords" }
                mines.add(MineData(coords))
            }
        }
        return mines
    }
}