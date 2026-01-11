package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.utils.hexDistance
import org.roldy.core.utils.hexRadius
import org.roldy.core.utils.randomColor
import org.roldy.core.x
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.data.mine.HarvestableType
import org.roldy.data.tile.SettlementTileData
import org.roldy.rendering.map.MapTerrainData
import kotlin.math.min
import kotlin.random.Random

class SettlementGenerator(
    val terrainData: Map<Vector2Int, MapTerrainData>,
    val mapData: MapData,
    override val occupied: (Vector2Int) -> Boolean
) : WorldGenerator<SettlementTileData> {
    val logger by logger()
    val harvestable = HarvestableType.harvestable
    val minSettlementDistance = 10
    val baseRegionSize = 1
    override fun generate(): List<SettlementTileData> {
        val mapSize = mapData.size
        val seed = mapData.seed
        val count = when (mapSize) {
            MapSize.Debug -> 1
            MapSize.ExtraLarge -> 80
            MapSize.Large -> 60
            MapSize.Medium -> 40
            MapSize.Small -> 20
        }
        val idGen = Random(seed + GeneratorSeeds.SETTLEMENT_SEED)
        val settlementRng = Random(seed + GeneratorSeeds.SETTLEMENT_SEED)
        val settlements = mutableListOf<SettlementTileData>()
        val attempts = count * 10 // Try multiple times to find good spots

        repeat(attempts) {
            if (settlements.size >= count) return@repeat

            val x = settlementRng.nextInt(mapSize.width)
            val y = settlementRng.nextInt(mapSize.height)
            val coords = x x y
            // Check if location is suitable
            val isSuitable =
                settlements.none {
                    hexDistance(x x y, it.coords) < minSettlementDistance + 1  // Min distance from others
                } && !occupied(coords) && terrainData.getValue(coords).terrain.biome.data.type != BiomeType.Water

            if (isSuitable) {
                val random = Random(seed + coords.sum)
                val maxHarvestable = random.nextInt(baseRegionSize) // settlement can have max of 6 mines
                val harvestableCount = min(harvestable.size, maxHarvestable)
                logger.debug {
                    """
                        
                        Generated settlement: $coords
                            Region size: $baseRegionSize
                            Harvestable size: $harvestableCount
                    """.trimIndent()
                }

                val baseRadius = hexRadius(coords, baseRegionSize, mapSize.min, mapSize.max).filter {
                    //remove hexes which are in bound of another settlement
                    val existing = settlements.flatMap(SettlementTileData::claims).toSet()
                    !existing.contains(it) && it.x in 0..<mapSize.width && it.y in 0..<mapSize.height
                }
                settlements.add(
                    SettlementTileData(
                        id = idGen.nextInt(),
                        coords,
                        "Settlement${settlements.size + 1}",
                        baseRadius,
                        harvestableCount,
                        random.nextInt(0, 3),
                        randomColor(random)
                    )
                )
            }
        }
        return settlements.apply {
            logger.debug {
                "Generated settlements: $size"
            }
        }
    }
}