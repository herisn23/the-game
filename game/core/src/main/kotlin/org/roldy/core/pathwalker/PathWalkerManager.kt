package org.roldy.core.pathwalker

import org.roldy.core.Vector2Int
import org.roldy.core.WorldPositioned
import org.roldy.core.utils.MoveUtils

class PathWalkerManager(
    val worldPositioned: WorldPositioned,
    val speed: (Vector2Int) -> Float,
    val onCoordsChanged: (Vector2Int) -> Unit,
) {
    private var currentPath: List<PathWalker.Node> = emptyList()
    private var currentPathIndex = 0
    private var coords: Vector2Int = Vector2Int(0, 0)

    var path: List<PathWalker.Node>
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
            onCoordsChanged(coords)
            //always set coords by nextPosition to correct resolving of next path if current path is still processing
            coords = nextWorldPos.coords
            MoveUtils.moveTowards(worldPositioned.position, nextWorldPos.position, speed(coords), deltaTime) {
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
}