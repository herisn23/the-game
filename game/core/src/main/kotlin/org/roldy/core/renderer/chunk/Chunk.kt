package org.roldy.core.renderer.chunk

import com.badlogic.gdx.math.Rectangle
import org.roldy.core.Vector2Int
import org.roldy.core.renderer.drawable.ChunkManagedDrawable


abstract class Chunk<T: ChunkObjectData>(val coords: Vector2Int, val chunkWidth: Float, val chunkHeight: Float) {
    class Object<T: ChunkObjectData>(
        val data: T,
        val drawable: ChunkManagedDrawable<T>,
    )

    internal val objects: MutableList<Object<T>> = mutableListOf()
    private val visibleObjects: MutableList<Object<T>> = mutableListOf()

    val allObjects: List<Object<T>> get() = objects

    fun filterForVisibleObjects(predicate: (Object<T>) -> Boolean): List<Object<T>> =
        run {
            this.visibleObjects.clear()
            objects.filterTo(this.visibleObjects, predicate)
        }

    val x = coords.x * chunkWidth
    val y = coords.y * chunkHeight
    val bounds = Rectangle(x, y, x + chunkWidth, y + chunkHeight)
}

