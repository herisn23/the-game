package org.roldy.rendering.g2d.chunk

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import org.roldy.core.coroutines.onGPUThread
import org.roldy.core.utils.invoke
import org.roldy.rendering.g2d.Diagnostics
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.Renderable
import org.roldy.rendering.g2d.Sortable
import org.roldy.rendering.g2d.drawable.ChunkManagedDrawable

class ChunkRenderer<D : ChunkObjectData, T : Chunk<D>>(
    private val camera: OrthographicCamera,
    private val chunkManager: ChunkManager<D, T>
) {
    private val viewBounds = Rectangle()
    private val active: MutableList<Layered> = mutableListOf()

    init {
        Diagnostics.addProvider {
            "Items rendered: ${active.size}"
        }
        Diagnostics.addProvider {
            "Persistent objects: ${chunkManager.persistentObjects.size}"
        }
        Diagnostics.addProvider {
            "Chunks visible: ${chunkManager.visibleChunks.size}"
        }
        Diagnostics.addProvider {
            "Chunks loaded: ${chunkManager.chunks.size}"
        }

        chunkManager.addListener(
            onGPUThread { data ->
                active.clear()
                active.addAll(data)
            }
        )
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
        chunkManager.update(view, camera.zoom)
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