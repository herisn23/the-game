package org.roldy.core.pathwalker

import org.roldy.core.PathWalker
import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.singleTask

/**
 * Asynchronous proxy that converts coordinate assignments into background pathfinding tasks.
 * Delegates computed paths via [onPathFound] when complete.
 */
class AsyncPathfindingProxy(
    private val pathfinder: Pathfinder,
    private val fromPosition: () -> Vector2Int,
    private val onPathFound: (PathWalker.Path) -> Unit
) {
    val task by singleTask()

    fun findPath(coords: Vector2Int) {
        task { main ->
            val path = pathfinder.findPath(fromPosition(), coords)
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