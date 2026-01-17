package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.data.configuration.biome.BiomeData
import org.roldy.data.map.MapSize
import org.roldy.data.state.GameState
import org.roldy.data.state.SettlementState
import org.roldy.data.tile.FoliageTileData
import org.roldy.rendering.map.MapTerrainData
import org.roldy.rendering.map.WorldMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

class StaticSpawnDataGenerator(
    val map: WorldMap,
    val alg: (Vector2Int, Random) -> Alg,
    val settlements: List<SettlementState>,
    val staticSpawnData: MapTerrainData.() -> List<BiomeData.StaticSpawnData>,
    override val occupied: (Vector2Int) -> Boolean
) : WorldGenerator<FoliageTileData> {
    val claims = settlements.flatMap { it.claims + it.reserved }
    private val logger by logger()

    companion object {
        fun cluster(
            map: WorldMap,
            gameState: GameState,
            occupied: (Vector2Int) -> Boolean,
            range: IntRange = 40..70
        ) =
            StaticSpawnDataGenerator(
                map,
                { start, rnd ->
                    Cluster(
                        start,
                        rnd,
                        rnd.nextInt(range.first, range.last),
                    )
                },
                gameState.settlements,
                { terrain.biome.data.trees },
                occupied
            )

        fun snake(
            map: WorldMap,
            gameState: GameState,
            occupied: (Vector2Int) -> Boolean,
            length: IntRange = 30..70,
            width: IntRange = 1..3
        ) =
            StaticSpawnDataGenerator(
                map,
                { start, rnd ->
                    Snake(
                        start,
                        rnd,
                        rnd.nextInt(length.first, length.last),
                        rnd.nextInt(width.first, width.last)
                    )
                },
                gameState.settlements,
                { terrain.biome.data.mountains },
                occupied
            )

    }

    fun inSettlement(coords: Vector2Int) =
        claims.any {
            it == coords
        }

    override fun generate(): List<FoliageTileData> =
        generatePlaces()
            .mapNotNull { (coords, spawnData) ->
                if (!occupied(coords) && !inSettlement(coords)) {
                    FoliageTileData(coords, spawnData)
                } else null
            }
            .apply {
                logger.debug {
                    "Generated spawnData: $size"
                }
            }

    private fun generatePlaces(): Map<Vector2Int, BiomeData.StaticSpawnData> {
        val result = mutableMapOf<Vector2Int, BiomeData.StaticSpawnData>()
        val mapIndex = when (map.data.size) {
            MapSize.Debug -> 0
            MapSize.ExtraLarge -> 4
            MapSize.Large -> 3
            MapSize.Medium -> 2
            MapSize.Small -> 1
        }
        repeat(1000 * mapIndex) { index ->

            val noodleRandom = Random(map.data.seed + index * 1000)
            val startCoords = map.terrainData.keys.random(noodleRandom)
            val startTerrain = map.terrainData[startCoords] ?: return@repeat

            // Get data for this biome
            val types = startTerrain.staticSpawnData()
            if (types.isEmpty()) return@repeat

            // Generate the noodle path
            val noodlePath = alg(startCoords, noodleRandom).generate(
                map
            )

            // Place along the path
            noodlePath.forEach { coords ->
                map.terrainData
                    .getValue(coords)
                    .let { terrainData ->
                        terrainData.staticSpawnData()
                            .filter { it.match(terrainData) }
                            .randomOrNull(noodleRandom)
                            ?.let { data ->
                                if (noodleRandom.nextFloat() < data.spawnChance) {
                                    result[coords] = data
                                }
                            }
                    }
            }
        }

        return result
    }


    interface Alg {
        fun generate(map: WorldMap): List<Vector2Int>
    }

    class Cluster(
        val start: Vector2Int,
        val random: Random,
        val size: Int
    ) : Alg {
        override fun generate(map: WorldMap): List<Vector2Int> {
            val cluster = mutableSetOf<Vector2Int>()
            val candidates = mutableSetOf<Vector2Int>()

            // Start with the initial position
            if (map.terrainData.containsKey(start)) {
                cluster.add(start)
                candidates.addAll(map.getNeighbors(start))
            } else {
                return emptyList()
            }

            // Grow the cluster until we reach the desired size
            while (cluster.size < size && candidates.isNotEmpty()) {
                // Pick a random candidate
                val candidate = candidates.random(random)
                candidates.remove(candidate)

                // Add it to the cluster if valid
                if (map.terrainData.containsKey(candidate) && candidate !in cluster) {
                    cluster.add(candidate)

                    // Add its neighbors as new candidates
                    map.getNeighbors(candidate).forEach { neighbor ->
                        if (neighbor !in cluster) {
                            candidates.add(neighbor)
                        }
                    }
                }
            }

            return cluster.toList()
        }
    }

    class Snake(
        val start: Vector2Int,
        val random: Random,
        val length: Int,
        val width: Int
    ) : Alg {
        override fun generate(map: WorldMap): List<Vector2Int> {
            val path = mutableListOf<Vector2Int>()
            var current = start
            var direction = random.nextFloat() * 2 * PI.toFloat()

            repeat(length) {
                // Add current position and width variations
                path.add(current)

                // Add width to create thicker noodles
                if (width > 1) {
                    for (w in 1 until width) {
                        val perpDir = direction + PI.toFloat() / 2
                        val offset1 = Vector2Int(
                            current.x + (cos(perpDir) * w).roundToInt(),
                            current.y + (sin(perpDir) * w).roundToInt()
                        )
                        val offset2 = Vector2Int(
                            current.x - (cos(perpDir) * w).roundToInt(),
                            current.y - (sin(perpDir) * w).roundToInt()
                        )
                        if (map.terrainData.containsKey(offset1)) path.add(offset1)
                        if (map.terrainData.containsKey(offset2)) path.add(offset2)
                    }
                }

                // Meander: slight random direction change
                direction += random.nextFloat() * 0.6f - 0.3f // ±0.3 radians (~17 degrees)

                // Step size variation for more organic feel
                val stepSize = if (random.nextFloat() < 0.3f) 2 else 1

                // Move to next position
                val next = Vector2Int(
                    current.x + (cos(direction) * stepSize).roundToInt(),
                    current.y + (sin(direction) * stepSize).roundToInt()
                )

                // Check if next position is valid
                if (!map.terrainData.containsKey(next)) {
                    // Try to find a nearby valid position
                    direction += random.nextFloat() * PI.toFloat() - PI.toFloat() / 2

                } else {
                    current = next
                }


            }

            return path.distinct()
        }
    }
}