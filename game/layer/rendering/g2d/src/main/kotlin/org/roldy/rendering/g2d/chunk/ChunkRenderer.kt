package org.roldy.rendering.g2d.chunk

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import org.roldy.core.utils.invoke
import org.roldy.rendering.g2d.Diagnostics
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.Renderable
import org.roldy.rendering.g2d.Sortable
import org.roldy.rendering.g2d.drawable.ChunkManagedDrawable

class ChunkRenderer<D : ChunkObjectData, T : Chunk<D>>(
    private val camera: OrthographicCamera,
    private val chunkManager: ChunkManager<D, T>,
    private val persistentObjects: List<Layered>
) {
    private val itemLevelCulling = false
    private val active = mutableListOf<Layered>()
    private val viewBounds = Rectangle()
    private val visibleDrawables = mutableListOf<ChunkManagedDrawable<D>>()

    init {
        Diagnostics.addProvider {
            "Items rendered: ${active.size}"
        }
        Diagnostics.addProvider {
            "Persistent objects: ${persistentObjects.size}"
        }
        Diagnostics.addProvider {
            "Chunks rendered: ${chunkManager.visibleChunks.size}"
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
        visibleDrawables.clear()

        // Chunk-level culling
        with(chunkManager) {
            update(view, camera.zoom)
            visibleDrawables += visibleChunks.flatMap { chunk ->
                when {
                    itemLevelCulling -> chunk.visibleObjects
                    else -> chunk.allObjects
                }
            }.map { it.drawable }
        }
        active.clear()

        // Create/reuse instances for visible items
        active.addAll(persistentObjects)
        active.addAll(visibleDrawables)
        // Y-sort for correct overlap
        active.sortWith(compareBy<Layered> { it.layer }      // Primary: layer ascending (-1, 0, 1, 2...)
            .thenByDescending { it.zIndex }  // Secondary: z-index descending
        )
    }

    context(delta: Float)
    fun render(batch: SpriteBatch) {
        update()
        batch.projectionMatrix = camera.combined
        batch {
            with(camera) {
                active.forEach {
                    when (it) {
                        is ChunkManagedDrawable<*> -> it.draw(batch)
                        is Renderable -> it.render()
                    }
                }
            }
        }
    }

    val objects: List<Sortable> get() = active
}