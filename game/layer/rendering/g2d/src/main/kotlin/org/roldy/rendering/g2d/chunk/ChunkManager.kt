package org.roldy.rendering.g2d.chunk

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Disposable
import org.roldy.core.*
import org.roldy.core.coroutines.EventTask
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.drawable.ChunkManagedDrawable
import org.roldy.rendering.g2d.drawable.DrawablePool
import kotlin.math.floor

fun interface PoolProvider<D : ChunkObjectData> {
    fun provide(): ChunkManagedDrawable<D>
}

abstract class ChunkManager<D : ChunkObjectData, T : Chunk<D>>(
    private val populator: ChunkPopulator<D, T>,
    val persistentObjects: List<Layered>,
    poolProvider: PoolProvider<D>
) : Disposable {
    protected val logger by logger()

    private val eventTask = EventTask(emitter = ::process)

    private val itemLevelCulling = true

    abstract val minCoords: Int
    abstract val maxCoords: Int

    protected abstract val chunkWidth: Float
    protected abstract val chunkHeight: Float

    internal val chunks = mutableMapOf<Vector2Int, T>()

    private val visibilityViewChunks = Rectangle()
    private val visibilityViewObjects = Rectangle()

    private val min = MutableVector2Int(0, 0)
    private val max = MutableVector2Int(0, 0)

    private val visibleChunksCache = mutableListOf<T>()

    val preloadRadius = -300f

    val visibleChunks: List<Chunk<D>> get() = visibleChunksCache.toList()

    private val pool = DrawablePool {
        poolProvider.provide()
    }

    init {
        eventTask.start()
    }

    private suspend fun process(): List<Layered> {
        updateVisibleChunks()
        // Chunk-level culling
        val visibleDrawables = visibleChunksCache.flatMap { chunk ->
            when {
                itemLevelCulling -> chunk.visibleObjects
                else -> chunk.allObjects
            }
        }.map { it.drawable }

        return (visibleDrawables + persistentObjects)
            .sortedWith(compareBy<Layered> { it.layer }      // Primary: layer ascending (-1, 0, 1, 2...)
                .thenByDescending { it.zIndex }  // Secondary: z-index descending
            )
    }

    fun addListener(listener: (List<Layered>) -> Unit) {
        eventTask.addListener(listener)
    }


    abstract fun getChunk(coords: Vector2Int): T

    // Provide / generate chunk data on demand
    internal fun getOrCreateChunk(coords: Vector2Int): T {
        return chunks.getOrPut(coords) {
            getChunk(coords).apply {
                objects.addAll(populator.populate(this).map {
                    val item = pool.obtain().apply {
                        data = it
                    }
                    Chunk.Object(it, item)
                })
            }
        }
    }

    internal val Chunk<D>.visibleObjects: List<Chunk.Object<D>>
        get() =
            filterForVisibleObjects {
                visibilityViewObjects.contains(it.data.position.x, it.data.position.y)
            }

    fun update(view: Rectangle, zoom: Float) {
        updateVisibilityViews(view, zoom)
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
                logger.debug { "Free object: in chunk ${it.key}, ${obj.data}" }
                pool.free(obj.drawable)
            }
        }
    }

    private fun MutableVector2Int.worldToChunk(
        x: Float,
        y: Float
    ): MutableVector2Int =
        apply {
            this.x = floor(x / chunkWidth).toInt()
            this.y = floor(y / chunkHeight).toInt()
        }

    override fun dispose() {
        eventTask.cancel()
    }

    var pause
        get() = eventTask.paused
        set(value) {
            eventTask.paused = value
        }
}
