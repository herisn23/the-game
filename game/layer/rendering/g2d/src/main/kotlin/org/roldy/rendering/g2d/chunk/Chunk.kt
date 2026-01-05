package org.roldy.rendering.g2d.chunk

import com.badlogic.gdx.math.Rectangle
import org.roldy.core.Vector2Int
import org.roldy.rendering.g2d.drawable.ChunkManagedDrawable


abstract class Chunk<T: ChunkObjectData>(val coords: Vector2Int, val chunkWidth: Float, val chunkHeight: Float) {
    class Object<T: ChunkObjectData>(
        val data: T,
        val drawable: ChunkManagedDrawable<T>,
    )

    internal val objects: MutableList<Object<T>> = mutableListOf()
    private val visibleObjects: MutableSet<Object<T>> = mutableSetOf()

    val allObjects: List<Object<T>> get() = objects

    fun filterForVisibleObjects(predicate: (Object<T>) -> Boolean): Set<Object<T>> =
        visibleObjects.apply {
            clear()
            addAll(objects.filter(predicate).toSet())
        }

    val x = coords.x * chunkWidth
    val y = coords.y * chunkHeight
    val bounds = Rectangle(x, y, x + chunkWidth, y + chunkHeight)
}

