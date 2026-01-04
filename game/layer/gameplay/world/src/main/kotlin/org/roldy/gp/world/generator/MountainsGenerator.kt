package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.x
import org.roldy.data.configuration.biome.BiomeData
import org.roldy.data.configuration.match
import org.roldy.data.tile.MountainTileData
import org.roldy.rendering.map.MapTerrainData
import org.roldy.rendering.map.WorldMap
import kotlin.random.Random

class MountainsGenerator(
    val map: WorldMap,
    override val occupied: (Vector2Int) -> Boolean
) : WorldGenerator<MountainTileData> {

    private val placedMountains = mutableListOf<Vector2Int>()

    override fun generate(): List<MountainTileData> {
        placedMountains.clear()

        return map.terrainData.toList().withIndex().mapNotNull { (index, mapData) ->
            val (coords, data) = mapData
            val mountain = findMountain(index, coords, data)
            mountain?.let {
                placedMountains.add(coords)
                MountainTileData(coords, it)
            }
        }
    }

    fun findMountain(index: Int, coords: Vector2Int, terrainData: MapTerrainData): BiomeData.SpawnData? {
        if (coords == 168 x 141) {
            println()
        }
        val random = Random(index + coords.sum + map.data.seed)
        if (occupied(coords)) return null
        return terrainData
            .mountainData()
            .filter { mountainData ->
                mountainData.match(terrainData) &&
                        !isTooCloseToOtherMountains(coords, mountainData.minRadius) &&
                        // Optional: Add some randomness to make it less dense
                        random.nextFloat() < mountainData.spawnChance // e.g., 0.3 = 30% chance
            }.randomOrNull(random)
    }

    private fun isTooCloseToOtherMountains(coords: Vector2Int, minRadius: Int): Boolean {
        return placedMountains.any { existingMountain ->
            coords.distanceTo(existingMountain) < minRadius
        }
    }

    fun MapTerrainData.mountainData() = terrain.biome.data.mountains

    private fun Vector2Int.distanceTo(other: Vector2Int): Float {
        val dx = (x - other.x).toFloat()
        val dy = (y - other.y).toFloat()
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}

// OPTIMIZED VERSION: Uses spatial grid for faster distance checks (for large maps)
class MountainsGeneratorOptimized(
    val map: WorldMap,
    override val occupied: (Vector2Int) -> Boolean
) : WorldGenerator<MountainTileData> {

    // Grid-based spatial partitioning for O(1) neighbor checks
    private val mountainGrid = mutableMapOf<Pair<Int, Int>, MutableList<Vector2Int>>()
    private val cellSize = 10 // Adjust based on typical minRadius

    override fun generate(): List<MountainTileData> {
        mountainGrid.clear()

        return map.terrainData.mapNotNull { (coords, data) ->
            val mountain = findMountain(coords, data)
            mountain?.let {
                addToGrid(coords)
                MountainTileData(coords, it)
            }
        }
    }

    fun findMountain(coords: Vector2Int, terrainData: MapTerrainData): BiomeData.SpawnData? {
        return terrainData
            .mountainData()
            .filter { mountainData ->
                mountainData.match(terrainData.noiseData) &&
                        !isTooCloseToOtherMountains(coords, mountainData.minRadius)
            }.randomOrNull()
    }

    private fun addToGrid(coords: Vector2Int) {
        val cell = getCellKey(coords)
        mountainGrid.getOrPut(cell) { mutableListOf() }.add(coords)
    }

    private fun getCellKey(coords: Vector2Int): Pair<Int, Int> {
        return Pair(coords.x / cellSize, coords.y / cellSize)
    }

    // Only check nearby grid cells instead of all mountains
    private fun isTooCloseToOtherMountains(coords: Vector2Int, minRadius: Int): Boolean {
        val cellRadius = (minRadius / cellSize) + 1
        val cell = getCellKey(coords)

        for (dx in -cellRadius..cellRadius) {
            for (dy in -cellRadius..cellRadius) {
                val checkCell = Pair(cell.first + dx, cell.second + dy)
                mountainGrid[checkCell]?.forEach { existingMountain ->
                    if (coords.distanceTo(existingMountain) < minRadius) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun MapTerrainData.mountainData() = terrain.biome.data.mountains

    private fun Vector2Int.distanceTo(other: Vector2Int): Float {
        val dx = (x - other.x).toFloat()
        val dy = (y - other.y).toFloat()
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}