package org.roldy.gp.world.pathfinding

import org.roldy.core.Vector2Int
import org.roldy.core.pathwalker.PathWalker
import org.roldy.rendering.map.WorldMap
import java.util.*
import kotlin.math.abs

/**
 * Pathfinding is calculating by tile walk cost.
 * 0 - means tile is immovable
 */
class TilePathfinder(
    private val worldMap: WorldMap,
    private val walkCost: (tile: Vector2Int, goal: Vector2Int) -> Float = { _, _ -> 1f }
) {
    val bestWalkCost = 10f

    // Get walkability cost (1f = easy, 0f = blocked, between = difficult)
    private fun getWalkability(tile: Vector2Int, goal: Vector2Int): Float {
        if (!worldMap.mapBounds.isInBounds(tile)) return 0f
        return walkCost(tile, goal).coerceIn(0f, bestWalkCost)
    }

    // Convert walkability to movement cost
    private fun getCost(tile: Vector2Int, goal: Vector2Int): Float {
        val walkability = getWalkability(tile, goal)
        if (walkability <= 0f) return Float.POSITIVE_INFINITY
        return bestWalkCost / walkability  // 1f -> cost 1.0, 0.5f -> cost 2.0, etc.
    }

    // Get the 6 neighbors of a hex tile (flat-top, stagger axis Y)
    private fun getNeighbors(tile: Vector2Int, goal: Vector2Int): List<Vector2Int> {
        val staggerIndex = worldMap.staggerIndex
        val isStaggered = if (staggerIndex == "even") {
            tile.y % 2 == 1
        } else {
            tile.y % 2 == 0
        }

        // Flat-top hex neighbors (6 directions)
        val neighbors = if (isStaggered) {
            listOf(
                Vector2Int(tile.x + 1, tile.y),
                Vector2Int(tile.x, tile.y - 1),
                Vector2Int(tile.x + 1, tile.y - 1),
                Vector2Int(tile.x - 1, tile.y),
                Vector2Int(tile.x, tile.y + 1),
                Vector2Int(tile.x + 1, tile.y + 1)
            )
        } else {
            listOf(
                Vector2Int(tile.x + 1, tile.y),
                Vector2Int(tile.x - 1, tile.y - 1),
                Vector2Int(tile.x, tile.y - 1),
                Vector2Int(tile.x - 1, tile.y),
                Vector2Int(tile.x - 1, tile.y + 1),
                Vector2Int(tile.x, tile.y + 1)
            )
        }

        // Don't filter here - let A* handle costs
        return neighbors.filter { worldMap.mapBounds.isInBounds(it) }
    }

    // Heuristic: Manhattan distance
    private fun heuristic(a: Vector2Int, b: Vector2Int): Float {
        return abs(a.x - b.x).toFloat() + abs(a.y - b.y).toFloat()
    }

    // A* pathfinding with weighted costs
    fun findPath(start: Vector2Int, goal: Vector2Int): PathWalker.Path {
        if (start == goal) {
            return PathWalker.Path(emptyList(), false)
        }

        // Check if goal is reachable
        if (getWalkability(goal, goal) <= 0f) {
            return PathWalker.Path(emptyList(), false)
        }

        val openSet = PriorityQueue<Tile>(compareBy { it.fScore })
        val closedSet = mutableSetOf<Vector2Int>()
        val cameFrom = mutableMapOf<Vector2Int, Vector2Int>()
        val gScore = mutableMapOf<Vector2Int, Float>().withDefault { Float.POSITIVE_INFINITY }

        gScore[start] = 0f
        openSet.add(Tile(start, 0f, heuristic(start, goal)))

        while (openSet.isNotEmpty()) {
            val current = openSet.poll()

            if (current.tile == goal) {
                // Reconstruct path
                val path = mutableListOf<PathWalker.Node>()
                var node = goal
                while (node in cameFrom) {
                    val position = worldMap.tilePosition.resolve(node)
                    val pathNode = PathWalker.Node(node, position)
                    path.add(0, pathNode)
                    node = cameFrom.getValue(node)
                }
                val position = worldMap.tilePosition.resolve(start)
                path.add(0, PathWalker.Node(start, position))
                return PathWalker.Path(path, true)
            }

            closedSet.add(current.tile)

            for (neighbor in getNeighbors(current.tile, goal)) {
                if (neighbor in closedSet) continue

                val movementCost = getCost(neighbor, goal)
                if (movementCost.isInfinite()) continue  // Skip unwalkable tiles

                val tentativeGScore = gScore.getValue(current.tile) + movementCost

                if (tentativeGScore < gScore.getValue(neighbor)) {
                    cameFrom[neighbor] = current.tile
                    gScore[neighbor] = tentativeGScore
                    val fScore = tentativeGScore + heuristic(neighbor, goal)

                    openSet.removeIf { it.tile == neighbor }
                    openSet.add(Tile(neighbor, tentativeGScore, fScore))
                }
            }
        }

        // No path found
        return PathWalker.Path(emptyList(), false)
    }

    private data class Tile(
        val tile: Vector2Int,
        val gScore: Float,
        val fScore: Float
    )
}