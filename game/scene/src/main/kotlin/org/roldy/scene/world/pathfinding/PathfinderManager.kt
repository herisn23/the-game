package org.roldy.scene.world.pathfinding

import org.roldy.core.TiledObject
import org.roldy.core.Vector2Int
import org.roldy.core.coroutines.singleTask
import org.roldy.core.x
import org.roldy.map.WorldMap

class PathfinderManager(
    private val map: WorldMap,
    private val fromPosition: () -> Vector2Int,
    private val onPathFound: (Path) -> Unit
) : TiledObject {

    val pathfinder = Pathfinder(map)
    val task by singleTask()
    private var lastCoords = 0 x 0
    override var coords: Vector2Int
        get() = lastCoords
        set(value) {
            lastCoords = value
            task {
                val path = pathfinder.findPath(fromPosition(), value)
                if (path.isComplete) {
                    onPathFound(path)
                }
            }
        }
}