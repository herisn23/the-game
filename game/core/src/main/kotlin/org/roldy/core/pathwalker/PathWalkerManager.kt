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
    private var currentPath: List<PathWalker.PathNode>? = null
    private var currentPathIndex = 0

    var path: List<PathWalker.PathNode>?
        get() = currentPath
        set(value) {
            currentPath = value
            currentPathIndex = 0
        }

    context(deltaTime: Float)
    fun walk() {
        // Follow the path
        currentPath?.let { path ->
            if (currentPathIndex < path.size) {
                val nextWorldPos = path[currentPathIndex]

                MoveUtils.moveTowards(worldPositioned.position, nextWorldPos.position, 500f, deltaTime) {
                    worldPositioned.position = nextWorldPos.position
                    coords = nextWorldPos.coords
                    currentPathIndex++

                    // Reached end of path
                    if (currentPathIndex >= path.size) {
                        currentPath = null
                        currentPathIndex = 0
                    }
                }?.let {
                    worldPositioned.position = it
                }
            }
        }
    }

    override var coords: Vector2Int = Vector2Int(0, 0)

}