package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.utils.hexDistance
import org.roldy.core.utils.hexRadius
import org.roldy.core.utils.randomColor
import org.roldy.core.x
import org.roldy.data.map.MapData
import org.roldy.data.mine.MineType
import org.roldy.gp.world.generator.data.SettlementData
import org.roldy.rendering.map.MapTerrainData
import kotlin.math.min
import kotlin.random.Random

class SettlementGenerator(
    val terrainData: Map<Vector2Int, MapTerrainData>,
    val mapData: MapData
) {
    val logger by logger()
    val harvestable = MineType.harvestable

    val maxRegionSize = mapData.size.width / mapData.size.settlements
    val minRegionSize = minOf(10, maxRegionSize)

    fun generate(): List<SettlementData> {
        val mapSize = mapData.size
        val seed = mapData.seed
        val count = mapSize.settlements
        val settlementRng = Random(seed + GeneratorSeeds.SETTLEMENT_SEED)
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
                    settlements.none {
                        hexDistance(x x y, it.coords) < mapSize.max.x / 4  // Min distance from others
                    }

            if (isSuitable) {
                val random = Random(seed + coords.sum)
                val regionSize = random.nextInt(minRegionSize, maxRegionSize) // tiles from settlement is 15
                val maxHarvestable = random.nextInt(regionSize) // settlement can have max of 6 mines
                val harvestableCount = min(harvestable.size, maxHarvestable)
                logger.debug {
                    """
                        
                        Generated settlement: $coords
                            Region size: $regionSize
                            Harvestable size: $harvestableCount
                    """.trimIndent()
                }

                val radius = hexRadius(coords, regionSize, mapSize.min, mapSize.max).filter {
                    //remove hexes which are in bound of another settlement
                    val existing = settlements.flatMap(SettlementData::radiusCoords).toSet()
                    !existing.contains(it)
                }
                settlements.add(
                    SettlementData(
                        coords,
                        "Settlement${settlements.size + 1}",
                        regionSize,
                        radius,
                        harvestableCount,
                        randomColor(random),
                        texture = "hexDirtCastle00_blue"
                    )
                )
            }
        }
        return settlements
    }
}