package org.roldy.scene.world.populator

import org.roldy.core.Vector2Int
import org.roldy.core.x
import org.roldy.map.WorldMapSize
import org.roldy.scene.distance
import org.roldy.scene.world.populator.environment.SettlementData
import org.roldy.terrain.TileData

object RoadsGenerator {
    data class Node(val x: Int, val y: Int, val cost: Float, val heuristic: Float) {
        val total = cost + heuristic
    }

    enum class RoadOrientation {
        HORIZONTAL,      // Left-Right
        VERTICAL,        // Up-Down
        CURVE_UP_RIGHT,  // Bottom-Left to Top-Right
        CURVE_UP_LEFT,   // Bottom-Right to Top-Left
        CURVE_DOWN_RIGHT,// Top-Left to Bottom-Right
        CURVE_DOWN_LEFT, // Top-Right to Bottom-Left
        T_UP,            // T with opening up
        T_DOWN,          // T with opening down
        T_LEFT,          // T with opening left
        T_RIGHT,         // T with opening right
        CROSS,           // 4-way intersection
        DEAD_END_UP,     // Dead end facing up
        DEAD_END_DOWN,   // Dead end facing down
        DEAD_END_LEFT,   // Dead end facing left
        DEAD_END_RIGHT   // Dead end facing right
    }

    data class RoadTile(val position: Vector2Int, val orientation: RoadOrientation)
    fun generate(
        settlements: List<SettlementData>,
        terrainData: Map<Vector2Int, TileData>,
        mapSize: WorldMapSize,
    ): List<RoadTile> {
        val roads = mutableSetOf<Vector2Int>()
        val connectedPairs = mutableSetOf<Set<SettlementData>>()

        // Connect each settlement to its nearest neighbor
        for (i in settlements.indices) {
            val start = settlements[i]

            // Find nearest settlement
            val nearest = settlements
                .filter { it != start }
                .minByOrNull { distance(start.coords.x, start.coords.y, it.coords.x, it.coords.y) }

            if (nearest != null) {
                val pair = setOf(start, nearest)
                // Only create road if this pair hasn't been connected yet
                if (pair !in connectedPairs) {
                    connectedPairs.add(pair)
                    roads.createRoad(
                        start.coords,
                        nearest.coords,
                        mapSize,
                        terrainData
                    )
                }
            }
        }

        // Calculate orientation for each road tile
        return roads.map { pos ->
            RoadTile(pos, calculateOrientation(pos, roads))
        }
    }

    private fun MutableSet<Vector2Int>.createRoad(
        start: Vector2Int,
        end: Vector2Int,
        mapSize: WorldMapSize,
        terrainData: Map<Vector2Int, TileData>
    ) {
        val startX = start.x
        val startY = start.y
        val endX = end.x
        val endY = end.y



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
                    add(pos.first x pos.second)
                    pos = cameFrom[pos]!!
                }
                return
            }

            // Check neighbors (only orthogonal - no diagonals)
            val directions = listOf(
                Pair(0, 1),   // up
                Pair(0, -1),  // down
                Pair(1, 0),   // right
                Pair(-1, 0)   // left
            )

            for ((dx, dy) in directions) {
                val nx = current.x + dx
                val ny = current.y + dy

                if (nx !in 0 until mapSize.size || ny !in 0 until mapSize.size) continue

                // Calculate cost based on terrain
                val terrainCost = terrainData.getTerrainCost(nx, ny)
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

    private fun Map<Vector2Int, TileData>.getTerrainCost(x: Int, y: Int): Float {
        val tile = getValue(x x y)
        return when {
            tile.noiseData.elevation < 0.3f -> 100f        // Water - very expensive
            tile.noiseData.elevation > 0.75f -> 50f        // Mountains - expensive
            tile.noiseData.moisture < 0.2f -> 30f       // Desert - moderate
            tile.noiseData.elevation in 0.4f..0.6f -> 1f   // Plains - cheap!
            else -> 10f               // Default
        }
    }

    private fun calculateOrientation(pos: Vector2Int, roads: Set<Vector2Int>): RoadOrientation {
        val x = pos.x
        val y = pos.y

        // Check all 4 directions
        val hasUp = roads.contains((x x (y + 1)))
        val hasDown = roads.contains((x x (y - 1)))
        val hasLeft = roads.contains(((x - 1) x y))
        val hasRight = roads.contains(((x + 1) x y))

        val connectionCount = listOf(hasUp, hasDown, hasLeft, hasRight).count { it }

        return when (connectionCount) {
            4 -> RoadOrientation.CROSS
            3 -> when {
                !hasUp -> RoadOrientation.T_DOWN
                !hasDown -> RoadOrientation.T_UP
                !hasLeft -> RoadOrientation.T_RIGHT
                !hasRight -> RoadOrientation.T_LEFT
                else -> RoadOrientation.CROSS // fallback
            }
            2 -> when {
                hasUp && hasDown -> RoadOrientation.VERTICAL
                hasLeft && hasRight -> RoadOrientation.HORIZONTAL
                hasUp && hasRight -> RoadOrientation.CURVE_UP_RIGHT
                hasUp && hasLeft -> RoadOrientation.CURVE_UP_LEFT
                hasDown && hasRight -> RoadOrientation.CURVE_DOWN_RIGHT
                hasDown && hasLeft -> RoadOrientation.CURVE_DOWN_LEFT
                else -> RoadOrientation.HORIZONTAL // fallback
            }
            1 -> when {
                hasUp -> RoadOrientation.DEAD_END_UP
                hasDown -> RoadOrientation.DEAD_END_DOWN
                hasLeft -> RoadOrientation.DEAD_END_LEFT
                hasRight -> RoadOrientation.DEAD_END_RIGHT
                else -> RoadOrientation.HORIZONTAL // fallback
            }
            else -> RoadOrientation.HORIZONTAL // isolated tile, default to horizontal
        }
    }

}