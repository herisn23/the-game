package org.roldy.core.pathwalker

import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.singleTask

/**
 * Asynchronous pathfinding manager that converts coordinate assignments into background pathfinding tasks.
 * Delegates computed paths via [onPathFound] when complete.
 */
class AsyncPathfindingManager(
    private val findPath: FindPath,
    private val fromPosition: () -> Vector2Int,
    private val onPathFound: (PathWalker.Path) -> Unit
) {
    val task by singleTask()

    fun findPath(coords: Vector2Int) {
        task { main ->
            val path = findPath(fromPosition(), coords)
            main {
                if (path.isComplete) {
                    onPathFound(path)
                }
            }
        }
    }

    fun stop() {
        task.cancel()
    }
}