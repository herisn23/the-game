package org.roldy.core.pathwalker

import org.roldy.core.PathWalker
import org.roldy.core.Vector2Int

fun interface Pathfinder {
    fun findPath(start: Vector2Int, goal: Vector2Int): PathWalker.Path
}