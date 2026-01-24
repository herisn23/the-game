package org.roldy.core.pathwalker

import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.singleTask

/**
 * Asynchronous pathfinding manager that converts coordinate assignments into background pathfinding tasks.
 */
class AsyncPathfindingManager(
    private val findPath: FindPath
) {
    val task by singleTask()
    fun findPath(from: Vector2Int, to: Vector2Int, onPathFound: (PathWalker.Path) -> Unit) {
        stop()
        task { main ->
            val path = this.findPath(from, to)
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