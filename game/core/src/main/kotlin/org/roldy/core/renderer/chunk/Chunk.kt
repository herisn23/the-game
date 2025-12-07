package org.roldy.core.renderer.chunk

import com.badlogic.gdx.math.Rectangle
import org.roldy.core.Vector2Int
import org.roldy.core.renderer.drawable.ChunkManagedDrawable


abstract class Chunk<T: ChunkObjectData>(val coords: Vector2Int, val size: Float) {
    class ChunkObject<T: ChunkObjectData>(
        val data: T,
        val drawable: ChunkManagedDrawable<T>,
    )

    internal val objects: MutableList<ChunkObject<T>> = mutableListOf()
    private val visibleObjects: MutableList<ChunkObject<T>> = mutableListOf()

    val allObjects: List<ChunkObject<T>> get() = objects

    fun filterForVisibleObjects(predicate: (ChunkObject<T>) -> Boolean): List<ChunkObject<T>> =
        run {
            this.visibleObjects.clear()
            objects.filterTo(this.visibleObjects, predicate)
        }

    val x = coords.x * size
    val y = coords.y * size
    val bounds = Rectangle(x, y, x + size, y + size)
}

