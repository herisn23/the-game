package org.roldy.core

import com.badlogic.gdx.math.Vector2

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
