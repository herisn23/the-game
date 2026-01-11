package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.data.configuration.biome.BiomeData
import org.roldy.data.map.Biome
import org.roldy.data.map.MapSize
import org.roldy.data.tile.MountainTileData
import org.roldy.rendering.map.MapTerrainData
import org.roldy.rendering.map.WorldMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

class MountainsGenerator(
    val map: WorldMap,
    val biomes: List<Biome>,
    override val occupied: (Vector2Int) -> Boolean
) : WorldGenerator<MountainTileData> {
    private val logger by logger()
    private val placedMountains = mutableSetOf<Vector2Int>()

    override fun generate(): List<MountainTileData> {

        val path = generateMountainSnakes()

        return path.mapNotNull { (coords, spawnData) ->
            if (!occupied(coords)) {
                placedMountains.add(coords)
                MountainTileData(coords, spawnData)
            } else null
        }.apply {
            logger.debug {
                "Generated mountains: $size"
            }
        }
    }

    private fun generateMountainSnakes(): List<Pair<Vector2Int, BiomeData.SpawnData>> {
        val result = mutableListOf<Pair<Vector2Int, BiomeData.SpawnData>>()
        val mountains = biomes.flatMap { it.data.mountains }
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

            // Get mountain data for this biome
            val mountainTypes = startTerrain.mountainData()
            if (mountainTypes.isEmpty()) return@repeat

            // Generate the noodle path
            val noodlePath = generateSnakePath(
                start = startCoords,
                random = noodleRandom,
                length = noodleRandom.nextInt(20, 60),
                width = noodleRandom.nextInt(1, 2)
            )

            // Place mountains along the path
            noodlePath.forEach { coords ->
                map.terrainData
                    .getValue(coords)
                    .let { terrainData ->
                        terrainData.mountainData()
                            .filter { it.match(terrainData) }
                            .randomOrNull(noodleRandom)
                            ?.let { mountainData ->
                                if (noodleRandom.nextFloat() < mountainData.spawnChance) {
                                    result.add(coords to mountainData)
                                }
                            }
                    }
            }
        }

        return result
    }

    private fun generateSnakePath(
        start: Vector2Int,
        random: Random,
        length: Int,
        width: Int
    ): List<Vector2Int> {
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

    fun MapTerrainData.mountainData() = terrain.biome.data.mountains
}