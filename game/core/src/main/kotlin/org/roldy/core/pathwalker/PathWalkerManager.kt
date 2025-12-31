package org.roldy.core.pathwalker

import org.roldy.core.Vector2Int
import org.roldy.core.WorldPositioned
import org.roldy.core.defaultWalkSpeed
import org.roldy.core.utils.MoveUtils

class PathWalkerManager(
    val worldPositioned: WorldPositioned,
    val walkCost: (Vector2Int) -> Float,
    val walker: TileWalker
) {
    private var currentPath: List<PathWalker.Node> = emptyList()
    private var currentPathIndex = 0
    private var tmpCoords: Vector2Int = walker.coords

    var path: List<PathWalker.Node>
        get() = currentPath
        set(value) {
            //current node is not finished yet, add i to new path
            val currentNode = currentPath.getOrNull(currentPathIndex)
            currentPath = currentNode?.let { listOf(it) + value } ?: value
            currentPathIndex = 0
        }

    fun calculateSpeed(coords: Vector2Int): Float {
        return defaultWalkSpeed * walker.speed * walkCost(coords)
    }

    context(deltaTime: Float)
    fun walk() {
        if (currentPathIndex < path.size) {
            val nextWorldPos = path[currentPathIndex]
            if (nextWorldPos != tmpCoords) {
                walker.onTileLeave(tmpCoords)
                walker.nextTile(nextWorldPos.coords)
            }
            //always set coords by nextPosition to correct resolving of next path if current path is still processing
            tmpCoords = nextWorldPos.coords
            MoveUtils.moveTowards(
                worldPositioned.position,
                nextWorldPos.position,
                calculateSpeed(tmpCoords),
                deltaTime
            ) {
                worldPositioned.position = nextWorldPos.position
                currentPathIndex++
                walker.onTileEnter(tmpCoords)
                // Reached end of path
                if (currentPathIndex >= path.size) {
                    walker.onPathEnd(tmpCoords)
                    currentPath = emptyList()
                    currentPathIndex = 0
                }
            }?.let {
                worldPositioned.position = it
            }
        }
    }
}