package org.roldy.scene.world

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.core.asset.loadAsset
import org.roldy.core.renderer.chunk.ChunkObjectData
import org.roldy.core.renderer.chunk.ChunkPopulator
import org.roldy.core.x
import org.roldy.map.WorldMapChunk
import org.roldy.map.WorldMapSize
import org.roldy.terrain.TileData
import kotlin.math.sqrt
import kotlin.random.Random

class WorldPopulator(
    private val terrainData: Map<Vector2Int, TileData>,
    private val mapSize: WorldMapSize,
    private val seed: Long
) : ChunkPopulator<WorldMapChunk> {
    private val biomeSprites = mapOf(
        "Forest" to "tile000",
        "Swamp" to "tile041",
        "Desert" to "tile025",
        "DeepDesert" to "tile105",
        "Ice" to "tile096",
        "Jungle" to "tile081",
        "Snow" to "tile073",
        "Savanna" to "tile064",
    )

    val settlementAtlas = TextureAtlas(loadAsset("House.atlas"))
    val trees = TextureAtlas(loadAsset("Road.atlas"))

    override fun populate(
        chunk: WorldMapChunk
    ): List<ChunkObjectData> =
        mutableListOf<ChunkObjectData>().apply {
            val data = terrainData.filterBy(chunk)
            val settlementsInChunk = settlements.filter { settlement ->
                data.contains(settlement.coords)
            }

            val roadsInChunk = roads.filter {
                data.contains(it)
            }
            this += roadsInChunk.map { road ->
                val position = chunk.objectPosition(road)
                ChunkObjectData(atlas = trees, name = "road", position = position, isRoad = true)
            }
            this += settlementsInChunk.map { settle ->
                val position = chunk.objectPosition(settle.coords)
                ChunkObjectData(atlas = settlementAtlas, name = settle.name, position = position, isRoad = false)
            }
//            this += data.map { (position, data) ->
//                fun rnd() = Math.random().toFloat()
//                val x = position.x * chunk.tileSize + rnd() * (chunk.tileSize / 2)
//                val y = position.y * chunk.tileSize + rnd() * (chunk.tileSize / 2)
//                ChunkObjectData(region = biomeSprites[data.terrain.biome.data.name]?:"", x = x, y = y)
//            }
        }

    private fun WorldMapChunk.objectPosition(coords: Vector2Int): Vector2 {
        val x = coords.x * tileSize + (tileSize / 2).toFloat()
        val y = coords.y * tileSize + (tileSize / 2).toFloat()
        return x x y
    }

    private fun Map<Vector2Int, TileData>.filterBy(
        chunk: WorldMapChunk
    ) = run {
        chunk.tilesCoords.mapNotNull { coords ->
            this[coords]?.let {
                coords to it
            }
        }.toMap()
    }

    private val settlementRng = Random(seed + 1)
    val roads = mutableListOf<Vector2Int>()
    val settlements = generateSettlements(20)

    data class Settlement(val coords: Vector2Int, val name: String)

    fun generateSettlements(count: Int): List<Settlement> {
        val settlements = mutableListOf<Settlement>()
        val attempts = count * 10 // Try multiple times to find good spots

        repeat(attempts) {
            if (settlements.size >= count) return@repeat

            val x = settlementRng.nextInt(mapSize.size)
            val y = settlementRng.nextInt(mapSize.size)
            val coords = x x y
            val tile = terrainData.getValue(coords)

            // Check if location is suitable
            val isSuitable = tile.elevation in 0.35f..0.65f &&  // Not too high/low
                    tile.moisture > 0.3f &&           // Not desert
                    tile.temperature in 0.3f..0.7f &&        // Temperate
                    settlements.none {
                        distance(x, y, it.coords.x, it.coords.y) < 15  // Min distance from others
                    }

            if (isSuitable) {
                settlements.add(Settlement(coords, "Settlement${settlements.size + 1}"))
            }
        }

        return settlements.also(::generateRoads)
    }

    fun generateRoads(settlements: List<Settlement>) {
        // Connect each settlement to its nearest neighbor
        for (i in settlements.indices) {
            val start = settlements[i]

            // Find nearest settlement
            val nearest = settlements
                .filter { it != start }
                .minByOrNull { distance(start.coords.x, start.coords.y, it.coords.x, it.coords.y) }

            if (nearest != null) {
                createRoad(start.coords.x, start.coords.y, nearest.coords.x, nearest.coords.y)
            }
        }
    }
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
                    roads.add(pos.first x pos.second)
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

                    if (nx !in 0 until mapSize.size || ny !in 0 until mapSize.size) continue

                    // Calculate cost based on terrain
                    val terrainCost = getTerrainCost(nx, ny)
                    val newCost = costSoFar[Pair(current.x, current.y)]!! + terrainCost

                    val neighbor = Pair(nx, ny)
                    if (neighbor !in costSoFar || newCost < costSoFar[neighbor]!!) {
                        costSoFar[neighbor] = newCost
                        cameFrom[neighbor] = Pair(current.x, current.y)
                        openSet.add(
                            Node(
                                nx, ny, newCost,
                                distance(nx, ny, endX, endY)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getTerrainCost(x: Int, y: Int): Float {
        val tile = terrainData.getValue(x x y)
        return when {
            tile.elevation < 0.3f -> 100f        // Water - very expensive
            tile.elevation > 0.75f -> 50f        // Mountains - expensive
            tile.moisture < 0.2f -> 30f       // Desert - moderate
            tile.elevation in 0.4f..0.6f -> 1f   // Plains - cheap!
            else -> 10f               // Default
        }
    }

    private fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        val dx = (x1 - x2).toFloat()
        val dy = (y1 - y2).toFloat()
        return sqrt(dx * dx + dy * dy)
    }
}