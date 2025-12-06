package org.roldy.core.renderer.chunk

import com.badlogic.gdx.math.Rectangle
import org.roldy.core.MutableVector2Int
import org.roldy.core.Vector2Int
import org.roldy.core.repeat
import org.roldy.core.x
import kotlin.math.floor


abstract class ChunkDataManager<T : Chunk>(
    private val populator: ChunkPopulator<T>,
) {
    abstract val minCoords: Int
    abstract val maxCoords: Int
    protected abstract val chunkSize: Float
    val preloadRadius = -100f
    internal val chunks = mutableMapOf<Vector2Int, T>()

    abstract fun getChunk(coords: Vector2Int): T

    // Provide / generate chunk data on demand
    internal fun getOrCreateChunk(coords: Vector2Int): T {
        return chunks.getOrPut(coords) {
            getChunk(coords).apply {
                objects.addAll(populator.populate(this))
            }
        }
    }

    private val visibilityViewChunks = Rectangle()
    private val visibilityViewObjects = Rectangle()
    internal val Chunk.visibleObjects
        get() =
            filterForVisibleObjects {
                visibilityViewObjects.contains(it.position.x, it.position.y)
            }

    fun updateView(view: Rectangle, zoom: Float) {
        val radius = preloadRadius * zoom
        visibilityViewChunks.set(
            view.x + radius,
            view.y + radius,
            view.x + view.width - radius,
            view.y + view.height - radius
        )
        val itemMargin = radius * 2
        visibilityViewObjects.set(
            view.x + radius,
            view.y + radius,
            view.width - itemMargin,
            view.height - itemMargin,
        )
    }


    val min = MutableVector2Int(0, 0)
    val max = MutableVector2Int(0, 0)
    val visibleChunksCache = mutableListOf<T>()
    internal val visibleChunks: List<T>
        get() {
            visibleChunksCache.clear()
            min.worldToChunk(visibilityViewChunks.x, visibilityViewChunks.y)
            max.worldToChunk(visibilityViewChunks.width, visibilityViewChunks.height)


            repeat(
                maxOf(minCoords, min.x)..minOf(maxCoords, max.x),
                maxOf(minCoords, min.y)..minOf(maxCoords, max.y)
            ) { x, y ->
                visibleChunksCache += getOrCreateChunk(x x y)
            }
            return visibleChunksCache
        }

    fun unload() {
        val toUnload = chunks.filter {
            !visibleChunks.contains(it.value)
        }
        toUnload.forEach {
            chunks.remove(it.key)?.objects?.clear()
        }
    }

    private fun MutableVector2Int.worldToChunk(
        x: Float,
        y: Float
    ): MutableVector2Int =
        apply {
            this.x = floor(x / chunkSize).toInt()
            this.y = floor(y / chunkSize).toInt()
        }
}
