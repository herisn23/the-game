package org.roldy.scene.world.pathfinding

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.core.pathwalker.PathWalker
import org.roldy.map.WorldMap
import java.util.*
import kotlin.math.abs

// Pathfinding result
data class Path(
    val tiles: List<PathWalker.PathNode>,
    val isComplete: Boolean  // false if no path found
) {
    data class Node(
        override val coords: Vector2Int,
        override val position: Vector2
    ):PathWalker.PathNode
}

class Pathfinder(
    private val worldMap: WorldMap
) {
    private val layer = worldMap.tiledMap.layers[1] as TiledMapTileLayer

    // Check if a tile is walkable (override this based on your game logic)
    private fun isWalkable(tile: Vector2Int): Boolean {
        if (!worldMap.mapBounds.isInBounds(tile)) return false

        val cell = layer.getCell(tile.x, tile.y) ?: return false

        // Example: check tile properties or tile ID
        // For now, assume all tiles are walkable
        return true
    }

    // Get the 6 neighbors of a hex tile (flat-top, stagger axis Y)
    private fun getNeighbors(tile: Vector2Int): List<Vector2Int> {
        val staggerIndex = worldMap.tiledMap.properties.get("staggerindex", String::class.java) ?: "even"
        val isStaggered = if (staggerIndex == "even") {
            tile.y % 2 == 1
        } else {
            tile.y % 2 == 0
        }

        // Flat-top hex neighbors (6 directions)
        val neighbors = if (isStaggered) {
            // Staggered row (offset to the right)
            listOf(
                Vector2Int(tile.x + 1, tile.y),     // Right
                Vector2Int(tile.x, tile.y - 1),     // Down-left
                Vector2Int(tile.x + 1, tile.y - 1), // Down-right
                Vector2Int(tile.x - 1, tile.y),     // Left
                Vector2Int(tile.x, tile.y + 1),     // Up-left
                Vector2Int(tile.x + 1, tile.y + 1)  // Up-right
            )
        } else {
            // Non-staggered row
            listOf(
                Vector2Int(tile.x + 1, tile.y),     // Right
                Vector2Int(tile.x - 1, tile.y - 1), // Down-left
                Vector2Int(tile.x, tile.y - 1),     // Down-right
                Vector2Int(tile.x - 1, tile.y),     // Left
                Vector2Int(tile.x - 1, tile.y + 1), // Up-left
                Vector2Int(tile.x, tile.y + 1)      // Up-right
            )
        }

        return neighbors.filter(::isWalkable)
    }

    // Heuristic: Manhattan distance (good enough for hex grids)
    private fun heuristic(a: Vector2Int, b: Vector2Int): Float {
        return abs(a.x - b.x).toFloat() + abs(a.y - b.y).toFloat()
    }

    // A* pathfinding
    fun findPath(start: Vector2Int, goal: Vector2Int): Path {
        if (!isWalkable(goal)) {
            return Path(emptyList(), false)
        }

        if (start == goal) {
            return Path(emptyList(), false)
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
                val path = mutableListOf<Path.Node>()
                var node = goal
                while (node in cameFrom) {
                    val position = worldMap.tilePosition.resolve(node)
                    val pathNode = Path.Node(node, position)
                    path.add(0, pathNode)
                    node = cameFrom.getValue(node)
                }
                val position = worldMap.tilePosition.resolve(start)
                path.add(0, Path.Node(start, position))
                return Path(path, true)
            }

            closedSet.add(current.tile)

            for (neighbor in getNeighbors(current.tile)) {
                if (neighbor in closedSet) continue

                val tentativeGScore = gScore.getValue(current.tile) + 1f

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
        return Path(emptyList(), false)
    }

    private data class Tile(
        val tile: Vector2Int,
        val gScore: Float,
        val fScore: Float
    )
}