package org.roldy.core.renderer.chunk

import com.badlogic.gdx.math.Rectangle
import org.roldy.core.Vector2Int


abstract class Chunk(val coords: Vector2Int, val size: Float) {
    internal val objects: MutableList<ChunkObjectData> = mutableListOf()
    private val visibleObjects: MutableList<ChunkObjectData> = mutableListOf()

    val allObjects: List<ChunkObjectData> get() = objects

    fun filterForVisibleObjects(predicate: (ChunkObjectData) -> Boolean): List<ChunkObjectData> =
        run {
            this.visibleObjects.clear()
            objects.filterTo(this.visibleObjects, predicate)
        }

    val x = coords.x * size
    val y = coords.y * size
    val bounds = Rectangle(x, y, x + size, y + size)
}

