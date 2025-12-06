package org.roldy.core.renderer

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import org.roldy.core.Diagnostics
import org.roldy.core.Renderable
import org.roldy.core.renderer.chunk.Chunk
import org.roldy.core.renderer.chunk.ChunkDataManager
import org.roldy.core.renderer.chunk.ChunkObjectData
import org.roldy.core.renderer.drawable.Drawable
import org.roldy.core.renderer.drawable.DrawablePool
import org.roldy.utils.invoke

fun interface StaticObjectBuilder {
    fun build(): Drawable
}
class ChunkRenderer<T : Chunk>(
    private val camera: OrthographicCamera,
    private val chunkManager: ChunkDataManager<T>,
    private val persistentItems: List<Sortable>,
    staticObjectBuilder: StaticObjectBuilder
) {
    private val itemLevelCulling = false
    private val pool = DrawablePool {
        staticObjectBuilder.build()
    }
    private val poolableItems = mutableListOf<Drawable>() // currently visible instances

    private val active = mutableListOf<Sortable>()
    private val viewBounds = Rectangle()

    init {
        Diagnostics.addProvider {
            "Items rendered: ${active.size}"
        }
        Diagnostics.addProvider {
            "Persistent objects: ${persistentItems.size}"
        }
        Diagnostics.addProvider {
            "Chunks rendered: ${chunkManager.visibleChunksCache.size}"
        }
        Diagnostics.addProvider {
            "Chunks loaded: ${chunkManager.chunks.size}"
        }
    }

    private fun computeViewBounds(): Rectangle {
        val effectiveViewportWidth = camera.viewportWidth * camera.zoom
        val effectiveViewportHeight = camera.viewportHeight * camera.zoom
        viewBounds.set(
            camera.position.x - effectiveViewportWidth / 2f,
            camera.position.y - effectiveViewportHeight / 2f,
            effectiveViewportWidth,
            effectiveViewportHeight
        )
        return viewBounds
    }

    private fun update() {
        val view = computeViewBounds()

        // Collect items in visible chunks
        val visibleItems = mutableListOf<ChunkObjectData>()

        // Chunk-level culling
        with(chunkManager) {
            updateView(view, camera.zoom)
            visibleChunks.forEach { chunk ->
                visibleItems +=
                    when {
                        itemLevelCulling -> chunk.visibleObjects
                        else -> chunk.allObjects
                    }
            }
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
        active.sortWith(compareBy<Sortable> { it.layer }      // Primary: layer ascending (-1, 0, 1, 2...)
            .thenByDescending { it.zIndex }  // Secondary: z-index descending
        )
    }

    context(delta: Float)
    fun render(batch: SpriteBatch) {
        update()

        batch.projectionMatrix = camera.combined
        batch {
            active.forEach {
                when (it) {
                    is Drawable -> it.draw(batch)
                    is Renderable -> it.render()
                }
            }
        }

        chunkManager.unload()
    }
}
