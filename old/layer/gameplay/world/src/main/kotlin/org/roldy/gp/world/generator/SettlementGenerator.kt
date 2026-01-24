package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.plus
import org.roldy.core.utils.hexDistance
import org.roldy.core.utils.randomBrightColor
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
    val maxClaimsCount = 200
    val minClaimsCount = 100
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
        val minBorderDistance = 10
        repeat(attempts) {
            if (settlements.size >= count) return@repeat

            val x = settlementRng.nextInt(minBorderDistance, mapSize.width - minBorderDistance)
            val y = settlementRng.nextInt(minBorderDistance, mapSize.height - minBorderDistance)
            val coords = x x y
            // Check if location is suitable
            val isSuitable =
                settlements.none {
                    hexDistance(x x y, it.coords) < minSettlementDistance + 1  // Min distance from others
                } && !occupied(coords) && terrainData.getValue(coords).terrain.biome.data.type != BiomeType.Water

            if (isSuitable) {
                fun List<Vector2Int>.toWorldCoords() =
                    map { it + coords }

                val random = Random(seed + coords.sum)
                val type = random.nextInt(0, 3)
                val reserved = type.reservedTiles(coords.y % 2 == 0)
                val claims = generateClaims(reserved, random.nextInt(minClaimsCount, maxClaimsCount + 1))
                val maxHarvestable = random.nextInt(10, claims.size / 2) // settlement can have max of 6 mines
                val harvestableCount = min(harvestable.size, maxHarvestable)
                logger.debug {
                    """
                        
                        Generated settlement: $coords
                            Harvestable size: $harvestableCount
                    """.trimIndent()
                }

                settlements.add(
                    SettlementTileData(
                        id = idGen.nextInt(),
                        coords,
                        "Settlement${settlements.size + 1}",
                        claims.toWorldCoords(),
                        reserved.toWorldCoords(),
                        harvestableCount,
                        type,
                        randomBrightColor(random)
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

    fun generateClaims(reserved: List<Vector2Int>, count: Int): List<Vector2Int> {
        if (reserved.isEmpty()) return emptyList()

        val claims = reserved.toMutableSet()
        val random = Random.Default

        val hexDirections = listOf(
            Vector2Int(1, 0), Vector2Int(-1, 0),
            Vector2Int(0, 1), Vector2Int(1, 1),
            Vector2Int(0, -1), Vector2Int(1, -1)
        )

        val frontier = mutableSetOf<Vector2Int>()

        // Initialize frontier
        reserved.forEach { coord ->
            hexDirections.forEach { dir ->
                val neighbor = coord + dir
                if (neighbor !in claims) {
                    frontier.add(neighbor)
                }
            }
        }

        var added = 0
        while (added < count && frontier.isNotEmpty()) {
            val newClaim = frontier.random(random)
            claims.add(newClaim)
            frontier.remove(newClaim)
            added++

            // Expand frontier
            hexDirections.forEach { dir ->
                val neighbor = newClaim + dir
                if (neighbor !in claims) {
                    frontier.add(neighbor)
                }
            }
        }
        claims.removeAll(reserved.toSet())
        return claims.toList()
    }

    fun Int.reservedTiles(even: Boolean): List<Vector2Int> {
        val base = if (even) {
            listOf(
                0 x 0,
                -1 x 1,
                0 x 1,
                1 x 0,
            )
        } else {
            listOf(
                0 x 0,
                1 x 0,
                0 x 1,
                1 x 1,
            )
        }
        val additional = when (this) {
            2 -> if (even) {
                listOf(
                    1 x 1,
                    2 x 0,
                    2 x 1
                )
            } else {
                listOf(
                    2 x 1,
                    2 x 0,
                    3 x 1
                )
            }

            else -> emptyList()

        }
        return when (this) {
            2 -> base + additional
            else -> base
        }
    }
}