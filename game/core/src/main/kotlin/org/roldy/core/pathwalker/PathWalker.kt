package org.roldy.core.pathwalker

import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int

interface PathWalker {
    interface PathNode {
        val position: Vector2
        val coords: Vector2Int
    }

    fun pathWalking(path: List<PathNode>)

}