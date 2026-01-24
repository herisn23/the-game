package org.roldy.core.pathwalker

import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int

typealias FindPath = (start: Vector2Int, goal: Vector2Int) -> PathWalker.Path

interface PathWalker {
    data class Node(
        val coords: Vector2Int,
        val position: Vector2
    )

    data class Path(
        val tiles: List<Node>,
        val isComplete: Boolean  // false if no path found
    )

    fun pathWalking(path: Path)
}