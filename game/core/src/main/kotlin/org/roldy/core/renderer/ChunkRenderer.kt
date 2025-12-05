package org.roldy.core.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
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

class ChunkDebugRenderer(
    private val camera: OrthographicCamera,
) {
    val font = BitmapFont().apply {
        data.setScale(3f)
        color = Color.GREEN
    }
    val chunks = mutableListOf<Chunk>()
    val batch = SpriteBatch()
    fun render() {
        batch.projectionMatrix = camera.combined
        batch {
            chunks.forEach { chunk ->
                font.draw(
                    this, "(${chunk.coords.x}, ${chunk.coords.y})",
                    chunk.bounds.x,
                    chunk.bounds.y + 50f,
                )
            }
        }

    }
}

class ChunkRenderer<T : Chunk>(
    private val camera: OrthographicCamera,
    private val chunkManager: ChunkDataManager<T>,
    private val persistentItems: List<Sortable>,
    staticObjectBuilder: StaticObjectBuilder
) {
    private val chunkDebugRenderer = ChunkDebugRenderer(camera)
    private val itemLevelCulling = true
    private val pool = DrawablePool {
        staticObjectBuilder.build()
    }
    private val poolableItems = mutableListOf<Drawable>() // currently visible instances

    private val active = mutableListOf<Sortable>()
    private val viewBounds = Rectangle()

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

    fun update() {
        val view = computeViewBounds()

        // Collect items in visible chunks
        val visibleItems = mutableListOf<ChunkObjectData>()
        chunkDebugRenderer.chunks.clear()

        // Chunk-level culling
        with(chunkManager) {
            updateView(view, camera.zoom)
            chunksInView.forEach { chunk ->
                chunkDebugRenderer.chunks.add(chunk)
                visibleItems +=
                    when {
                        itemLevelCulling -> chunk.visibleItems
                        else -> chunk.allItems
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
        chunkDebugRenderer.render()
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            println("Items: ${active.filter { it is Drawable }.size}")
            println("Chunks: ${chunkDebugRenderer.chunks.size}")
        }
    }
}
