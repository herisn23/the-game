package org.roldy.core.pathwalker

import org.roldy.core.TilePositioned
import org.roldy.core.Vector2Int
import org.roldy.core.WorldPositioned
import org.roldy.core.logger
import org.roldy.core.utils.MoveUtils

class PathWalkerManager(
    val worldPositioned: WorldPositioned
) : TilePositioned {
    val logger by logger()
    private var currentPath: List<PathWalker.PathNode> = emptyList()
    private var currentPathIndex = 0

    var path: List<PathWalker.PathNode>
        get() = currentPath
        set(value) {
            //current node is not finished yet, add i to new path
            val currentNode = currentPath.getOrNull(currentPathIndex)
            currentPath = currentNode?.let { listOf(it) + value } ?: value
            currentPathIndex = 0
        }

    context(deltaTime: Float)
    fun walk() {
        if (currentPathIndex < path.size) {
            val nextWorldPos = path[currentPathIndex]
            //always set coords by nextPosition to correct resolving of next path if current path is still processing
            coords = nextWorldPos.coords
            MoveUtils.moveTowards(worldPositioned.position, nextWorldPos.position, 500f, deltaTime) {
                worldPositioned.position = nextWorldPos.position
                currentPathIndex++

                // Reached end of path
                if (currentPathIndex >= path.size) {
                    currentPath = emptyList()
                    currentPathIndex = 0
                }
            }?.let {
                worldPositioned.position = it
            }
        }
    }

    override var coords: Vector2Int = Vector2Int(0, 0)

}