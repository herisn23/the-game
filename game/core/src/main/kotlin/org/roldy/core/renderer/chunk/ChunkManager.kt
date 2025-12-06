package org.roldy.core.renderer.chunk

import com.badlogic.gdx.math.Rectangle
import org.roldy.core.MutableVector2Int
import org.roldy.core.Vector2Int
import org.roldy.core.renderer.drawable.Drawable
import org.roldy.core.renderer.drawable.DrawablePool
import org.roldy.core.repeat
import org.roldy.core.x
import kotlin.math.floor

fun interface DrawableBuilder<T: ChunkObjectData> {
    fun build(): Drawable<T>
}

abstract class ChunkManager<D : ChunkObjectData, T : Chunk<D>>(
    private val populator: ChunkPopulator<D, T>,
    drawableBuilder: DrawableBuilder<D>
) {
    abstract val minCoords: Int
    abstract val maxCoords: Int
    protected abstract val chunkSize: Float
    internal val chunks = mutableMapOf<Vector2Int, T>()
    private val visibilityViewChunks = Rectangle()
    private val visibilityViewObjects = Rectangle()
    private val min = MutableVector2Int(0, 0)
    private val max = MutableVector2Int(0, 0)
    private val visibleChunksCache = mutableListOf<T>()
    val preloadRadius = -100f
    val visibleChunks: List<T> get() = visibleChunksCache
    private val pool = DrawablePool {
        drawableBuilder.build()
    }

    abstract fun getChunk(coords: Vector2Int): T

    // Provide / generate chunk data on demand
    internal fun getOrCreateChunk(coords: Vector2Int): T {
        return chunks.getOrPut(coords) {
            getChunk(coords).apply {
                objects.addAll(populator.populate(this).map {
                    val item = pool.obtain()
                    item.bind(it)
                    Chunk.ChunkObject(it, item)
                })
            }
        }
    }

    internal val Chunk<D>.visibleObjects
        get() =
            filterForVisibleObjects {
                visibilityViewObjects.contains(it.data.position.x, it.data.position.y)
            }

    fun update(view: Rectangle, zoom: Float) {
        updateVisibilityViews(view, zoom)
        updateVisibleChunks()
    }

    private fun updateVisibilityViews(view: Rectangle, zoom: Float) {
        //update visibility views
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

    private fun updateVisibleChunks() {
        visibleChunksCache.clear()

        min.worldToChunk(visibilityViewChunks.x, visibilityViewChunks.y)
        max.worldToChunk(visibilityViewChunks.width, visibilityViewChunks.height)

        repeat(
            maxOf(minCoords, min.x)..minOf(maxCoords, max.x),
            maxOf(minCoords, min.y)..minOf(maxCoords, max.y)
        ) { x, y ->
            visibleChunksCache += getOrCreateChunk(x x y)
        }
        //unload no longer visible chunks and their objects
        unload()
    }

    fun unload() {
        val toUnload = chunks.filter {
            !visibleChunksCache.contains(it.value)
        }
        toUnload.forEach {
            chunks.remove(it.key)?.objects?.forEach { obj ->
                pool.free(obj.drawable)
            }
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
