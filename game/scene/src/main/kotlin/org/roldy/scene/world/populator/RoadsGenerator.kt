package org.roldy.scene.world.populator

import org.roldy.core.Vector2Int
import org.roldy.core.x
import org.roldy.map.WorldMapSize
import org.roldy.scene.distance
import org.roldy.terrain.TileData

object RoadsGenerator {
    data class Node(val x: Int, val y: Int, val cost: Float, val heuristic: Float) {
        val total = cost + heuristic
    }
    fun generate(
        settlements: List<SettlementData>,
        terrainData: Map<Vector2Int, TileData>,
        mapSize: WorldMapSize,
    ): List<Vector2Int> {
        val roads = mutableListOf<Vector2Int>()
        // Connect each settlement to its nearest neighbor
        for (i in settlements.indices) {
            val start = settlements[i]

            // Find nearest settlement
            val nearest = settlements
                .filter { it != start }
                .minByOrNull { distance(start.coords.x, start.coords.y, it.coords.x, it.coords.y) }

            if (nearest != null) {
                roads.createRoad(
                    start.coords,
                    nearest.coords,
                    mapSize,
                    terrainData
                )
            }
        }
        return roads
    }

    private fun MutableList<Vector2Int>.createRoad(
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

            // Check neighbors
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx == 0 && dy == 0) continue

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
    }

    private fun Map<Vector2Int, TileData>.getTerrainCost(x: Int, y: Int): Float {
        val tile = getValue(x x y)
        return when {
            tile.elevation < 0.3f -> 100f        // Water - very expensive
            tile.elevation > 0.75f -> 50f        // Mountains - expensive
            tile.moisture < 0.2f -> 30f       // Desert - moderate
            tile.elevation in 0.4f..0.6f -> 1f   // Plains - cheap!
            else -> 10f               // Default
        }
    }

}