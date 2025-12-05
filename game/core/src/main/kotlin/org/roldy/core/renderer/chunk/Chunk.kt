package org.roldy.core.renderer.chunk

import com.badlogic.gdx.math.Rectangle
import org.roldy.core.Vector2Int


abstract class Chunk(val coords: Vector2Int, val size: Float) {
    internal val items: MutableList<ChunkObjectData> = mutableListOf()
    private val visibleItems: MutableList<ChunkObjectData> = mutableListOf()

    val allItems: List<ChunkObjectData> get() = items

    fun filterForVisibleItems(predicate: (ChunkObjectData) -> Boolean): List<ChunkObjectData> =
        run {
            this.visibleItems.clear()
            items.filterTo(this.visibleItems, predicate)
        }

    val x = coords.x * size
    val y = coords.y * size
    val bounds = Rectangle(x, y, x + size, y + size)
}

