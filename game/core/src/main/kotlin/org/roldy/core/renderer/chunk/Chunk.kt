package org.roldy.core.renderer.chunk

import com.badlogic.gdx.math.Rectangle
import org.roldy.core.Vector2Int
import org.roldy.core.renderer.drawable.Drawable


abstract class Chunk(val coords: Vector2Int, val size: Float) {
    class ChunkObject(
        val data: ChunkObjectData,
        val drawable: Drawable,
    )

    internal val objects: MutableList<ChunkObject> = mutableListOf()
    private val visibleObjects: MutableList<ChunkObject> = mutableListOf()

    val allObjects: List<ChunkObject> get() = objects

    fun filterForVisibleObjects(predicate: (ChunkObject) -> Boolean): List<ChunkObject> =
        run {
            this.visibleObjects.clear()
            objects.filterTo(this.visibleObjects, predicate)
        }

    val x = coords.x * size
    val y = coords.y * size
    val bounds = Rectangle(x, y, x + size, y + size)
}

