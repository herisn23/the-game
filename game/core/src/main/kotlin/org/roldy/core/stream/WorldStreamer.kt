package org.roldy.core.stream

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import org.roldy.core.Renderable
import org.roldy.core.stream.chunk.ChunkDataManager
import org.roldy.core.stream.chunk.ChunkItemData
import org.roldy.core.stream.drawable.Drawable
import org.roldy.core.stream.drawable.DrawablePool
import org.roldy.utils.invoke

class WorldStreamer(
    private val camera: OrthographicCamera,
    private val chunkManager: ChunkDataManager,
    private val persistentItems: List<Streamable>,
    instance: () -> Drawable
) {
    private val pool = DrawablePool(instance)
    private val poolableItems = mutableListOf<Drawable>() // currently visible instances

    private val active = mutableListOf<Streamable>()
    private val viewBounds = Rectangle()

    private fun computeViewBounds(): Rectangle {
        val w = camera.viewportWidth * camera.zoom
        val h = camera.viewportHeight * camera.zoom
        viewBounds.set(
            camera.position.x - w / 2f,
            camera.position.y - h / 2f,
            w, h
        )
        return viewBounds
    }

    fun update() {
        val view = computeViewBounds()

        // Collect items in visible chunks
        val visibleItems = mutableListOf<ChunkItemData>()
        for (chunk in chunkManager.chunksInView(view)) {
            visibleItems += chunk.items
        }

        // Return all active instances to pool
        poolableItems.forEach {
            pool.free(it)
        }
        poolableItems.clear()
        active.clear()

        // Create/reuse instances for visible items
        visibleItems.forEach { item ->
            val inst = pool.obtain()
            inst.bind(item)
            poolableItems += inst
        }
        active.addAll(persistentItems)
        active.addAll(poolableItems)
        // Y-sort for correct overlap
        active.sortBy { -it.zIndex }
    }

    context(delta: Float)
    fun render(batch: SpriteBatch) {
        batch.projectionMatrix = camera.combined
        batch {
            active.forEach {
                when (it) {
                    is Drawable -> it.draw(batch)
                    is Renderable -> it.render()
                }
            }
        }
    }
}
