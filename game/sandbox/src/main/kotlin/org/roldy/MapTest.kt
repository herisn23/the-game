package org.roldy

import kotlin.math.sqrt
import kotlin.random.Random

// Represents a tile on your map
data class Tile(
    val x: Int,
    val y: Int,
    val height: Float,      // 0.0 to 1.0
    val heat: Float,        // 0.0 to 1.0
    val moisture: Float     // 0.0 to 1.0
)

enum class ObjectType {
    TREE, CACTUS, ROCK, GRASS, NONE
}

data class Settlement(val x: Int, val y: Int, val name: String)

class ProceduralMapGenerator(
    val width: Int,
    val height: Int,
    val seed: Long
) {
    // Your existing terrain data
    private val tiles = Array(width) { Array(height) { Tile(0, 0, 0f, 0f, 0f) } }

    // Feature layers
    private val objects = Array(width) { Array(height) { ObjectType.NONE } }
    private val roads = Array(width) { BooleanArray(height) { false } }

    // Seeded RNGs for different features
    private val settlementRng = Random(seed + 1)
    private val roadRng = Random(seed + 2)
    private val objectRng = Random(seed + 3)

    // Initialize your terrain (you already have this)
    fun initializeTerrain() {
        // Your existing height, heat, moisture generation code
        // This is just a placeholder
        for (x in 0 until width) {
            for (y in 0 until height) {
                tiles[x][y] = Tile(x, y,
                    Random(seed + x * 1000L + y).nextFloat(),
                    Random(seed + x * 2000L + y).nextFloat(),
                    Random(seed + x * 3000L + y).nextFloat()
                )
            }
        }
    }

    // === OBJECT PLACEMENT ===

    fun placeObjects() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                val tile = tiles[x][y]

                // Use spatial hash for consistent per-tile randomness
                val tileRandom = Random(hashPosition(x, y, seed + 3))

                // Determine object based on terrain
                objects[x][y] = when {
                    // Water - no objects
                    tile.height < 0.3f -> ObjectType.NONE

                    // Mountains - rocks
                    tile.height > 0.75f && tileRandom.nextFloat() < 0.4f ->
                        ObjectType.ROCK

                    // Desert - cacti
                    tile.moisture < 0.3f && tile.heat > 0.6f && tileRandom.nextFloat() < 0.2f ->
                        ObjectType.CACTUS

                    // Forest - trees
                    tile.moisture > 0.5f && tile.heat > 0.3f && tile.height < 0.7f && tileRandom.nextFloat() < 0.6f ->
                        ObjectType.TREE

                    // Grassland
                    tile.moisture > 0.4f && tile.height > 0.3f && tile.height < 0.6f && tileRandom.nextFloat() < 0.3f ->
                        ObjectType.GRASS

                    else -> ObjectType.NONE
                }
            }
        }
    }

    // === SETTLEMENT PLACEMENT ===

    fun generateSettlements(count: Int): List<Settlement> {
        val settlements = mutableListOf<Settlement>()
        val attempts = count * 10 // Try multiple times to find good spots

        repeat(attempts) {
            if (settlements.size >= count) return@repeat

            val x = settlementRng.nextInt(width)
            val y = settlementRng.nextInt(height)
            val tile = tiles[x][y]

            // Check if location is suitable
            val isSuitable = tile.height in 0.35f..0.65f &&  // Not too high/low
                    tile.moisture > 0.3f &&           // Not desert
                    tile.heat in 0.3f..0.7f &&        // Temperate
                    settlements.none {
                        distance(x, y, it.x, it.y) < 15  // Min distance from others
                    }

            if (isSuitable) {
                settlements.add(Settlement(x, y, "Settlement${settlements.size + 1}"))
            }
        }

        return settlements
    }

    // === ROAD GENERATION ===

    fun generateRoads(settlements: List<Settlement>) {
        // Connect each settlement to its nearest neighbor
        for (i in settlements.indices) {
            val start = settlements[i]

            // Find nearest settlement
            val nearest = settlements
                .filter { it != start }
                .minByOrNull { distance(start.x, start.y, it.x, it.y) }

            if (nearest != null) {
                createRoad(start.x, start.y, nearest.x, nearest.y)
            }
        }
    }

    // Simple A* pathfinding for roads
    private fun createRoad(startX: Int, startY: Int, endX: Int, endY: Int) {
        data class Node(val x: Int, val y: Int, val cost: Float, val heuristic: Float) {
            val total = cost + heuristic
        }

        val openSet = mutableListOf(Node(startX, startY, 0f, distance(startX, startY, endX, endY)))
        val cameFrom = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        val costSoFar = mutableMapOf(Pair(startX, startY) to 0f)

        while (openSet.isNotEmpty()) {
            val current = openSet.minByOrNull { it.total } ?: break
            openSet.remove(current)

            if (current.x == endX && current.y == endY) {
                // Reconstruct path
                var pos = Pair(endX, endY)
                while (pos in cameFrom) {
                    roads[pos.first][pos.second] = true
                    pos = cameFrom[pos]!!
                }
                return
            }

            // Check neighbors
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx == 0 && dy == 0) continue

                    val nx = current.x + dx
                    val ny = current.y + dy

                    if (nx !in 0 until width || ny !in 0 until height) continue

                    // Calculate cost based on terrain
                    val terrainCost = getTerrainCost(nx, ny)
                    val newCost = costSoFar[Pair(current.x, current.y)]!! + terrainCost

                    val neighbor = Pair(nx, ny)
                    if (neighbor !in costSoFar || newCost < costSoFar[neighbor]!!) {
                        costSoFar[neighbor] = newCost
                        cameFrom[neighbor] = Pair(current.x, current.y)
                        openSet.add(Node(
                            nx, ny, newCost,
                            distance(nx, ny, endX, endY)
                        ))
                    }
                }
            }
        }
    }

    // Calculate movement cost for road pathfinding
    private fun getTerrainCost(x: Int, y: Int): Float {
        val tile = tiles[x][y]
        return when {
            tile.height < 0.3f -> 100f        // Water - very expensive
            tile.height > 0.75f -> 50f        // Mountains - expensive
            tile.moisture < 0.2f -> 30f       // Desert - moderate
            tile.height in 0.4f..0.6f -> 1f   // Plains - cheap!
            else -> 10f                        // Default
        }
    }

    // === UTILITY FUNCTIONS ===

    // Spatial hash for consistent per-tile randomness
    private fun hashPosition(x: Int, y: Int, seed: Long): Long {
        var hash = seed
        hash = hash * 31 + x
        hash = hash * 31 + y
        return hash
    }

    private fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        val dx = (x1 - x2).toFloat()
        val dy = (y1 - y2).toFloat()
        return sqrt(dx * dx + dy * dy)
    }

    // === GETTERS ===

    fun getObject(x: Int, y: Int) = objects[x][y]
    fun hasRoad(x: Int, y: Int) = roads[x][y]
    fun getTile(x: Int, y: Int) = tiles[x][y]

    // Print debug view
    fun printMap() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                val char = when {
                    roads[x][y] -> '═'
                    objects[x][y] == ObjectType.TREE -> '♣'
                    objects[x][y] == ObjectType.CACTUS -> 'i'
                    objects[x][y] == ObjectType.ROCK -> '●'
                    objects[x][y] == ObjectType.GRASS -> ','
                    tiles[x][y].height < 0.3f -> '≈'
                    tiles[x][y].height > 0.75f -> '▲'
                    else -> '·'
                }
                print(char)
            }
            println()
        }
    }
}

// === USAGE EXAMPLE ===

fun main() {
    val seed = 12345L
    val mapGen = ProceduralMapGenerator(80, 40, seed)

    // Generate terrain
    mapGen.initializeTerrain()

    // Place objects based on terrain
    mapGen.placeObjects()

    // Generate settlements
    val settlements = mapGen.generateSettlements(5)
    println("Generated ${settlements.size} settlements:")
    settlements.forEach { println("  ${it.name} at (${it.x}, ${it.y})") }

    // Generate roads between settlements
    mapGen.generateRoads(settlements)

    // Display the map
    println("\nMap Preview:")
    mapGen.printMap()

    println("\nLegend: ≈=water, ▲=mountain, ♣=tree, i=cactus, ●=rock, ,=grass, ═=road")
}