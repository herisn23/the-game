package org.roldy.core.stream

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle

class WorldStreamer(
    private val camera: OrthographicCamera,
    private val atlas: TextureAtlas,
    private val chunkManager: ChunkManager
) {
    private val pool = RenderItemPool(atlas)
    private val active = mutableListOf<RenderItem>() // currently visible instances
    private val tmpView = Rectangle()

    private fun computeViewBounds(): Rectangle {
        val w = camera.viewportWidth * camera.zoom
        val h = camera.viewportHeight * camera.zoom
        tmpView.set(
            camera.position.x - w / 2f,
            camera.position.y - h / 2f,
            w, h
        )
        return tmpView
    }

    fun updateVisible() {
        val view = computeViewBounds()

        // Collect items in visible chunks
        val visibleItems = mutableListOf<MapItemData>()
        for (chunk in chunkManager.chunksInView(view)) {
            for (item in chunk.items) {
                // Item-level culling
                if (view.contains(item.x, item.y)) {
                    visibleItems += item
                }
            }
        }

        // Return all active instances to pool
        active.forEach { pool.free(it) }
        active.clear()

        // Create/reuse instances for visible items
        visibleItems.forEach { item ->
            val inst = pool.obtain()
            inst.bind(item, atlas)
            active += inst
        }

        // Y-sort for correct overlap
        active.sortBy { it.sprite.y }
    }

    fun render(batch: SpriteBatch) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        active.forEach { it.sprite.draw(batch) }
        batch.end()
    }
}
