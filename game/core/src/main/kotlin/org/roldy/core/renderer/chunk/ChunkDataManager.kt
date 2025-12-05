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
    val defaultMargin = -100f
    private val chunks = mutableMapOf<Vector2Int, T>()

    abstract fun getChunk(coords: Vector2Int): T

    // Provide / generate chunk data on demand
    internal fun getOrCreateChunk(coords: Vector2Int): T {
        return chunks.getOrPut(coords) {
            getChunk(coords).apply {
                items.addAll(populator.populate(this))
            }
        }
    }

    private val marginChunkView = Rectangle()
    private val marginItemsView = Rectangle()
    internal val Chunk.visibleItems
        get() =
            filterForVisibleItems {
                marginItemsView.contains(it.x, it.y)
            }

    fun updateView(view: Rectangle, zoom: Float) {
        val margin = defaultMargin * zoom
        marginChunkView.set(
            view.x + margin,
            view.y + margin,
            view.x + view.width - margin,
            view.y + view.height - margin
        )
        val itemMargin = margin * 2
        marginItemsView.set(
            view.x + margin,
            view.y + margin,
            view.width - itemMargin,
            view.height - itemMargin,
        )
    }


    val min = MutableVector2Int(0, 0)
    val max = MutableVector2Int(0, 0)
    val visibleChunks = mutableListOf<T>()
    internal val chunksInView: List<T>
        get() {
            visibleChunks.clear()
            min.worldToChunk(marginChunkView.x, marginChunkView.y)
            max.worldToChunk(marginChunkView.width, marginChunkView.height)


            repeat(
                maxOf(minCoords, min.x)..minOf(maxCoords, max.x),
                maxOf(minCoords, min.y)..minOf(maxCoords, max.y)
            ) { x, y ->
                visibleChunks += getOrCreateChunk(x x y)
            }
            return visibleChunks
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
