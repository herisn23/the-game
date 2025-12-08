package org.roldy.core.renderer

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.renderer.chunk.Chunk
import org.roldy.core.renderer.chunk.ChunkManager
import org.roldy.core.renderer.chunk.ChunkObjectData
import org.roldy.utils.invoke

class ChunkDebugRenderer<D : ChunkObjectData, T : Chunk<D>>(
    private val camera: OrthographicCamera,
    private val chunkManager: ChunkManager<D, T>,
) : AutoDisposableAdapter() {
    val font by disposable {
        BitmapFont().apply {
            data.setScale(3f)
            color = Color.GREEN
        }
    }
    val shapeRenderer = ShapeRenderer().disposable()
    val batch = SpriteBatch().disposable()

    fun render() {
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        chunkManager.visibleChunks.forEachIndexed { i, chunk ->

            shapeRenderer.color = Color.GREEN
            shapeRenderer.rect(
                chunk.bounds.x, chunk.bounds.y,
                chunk.bounds.width, chunk.bounds.height
            )

        }
        shapeRenderer.end()
        batch.projectionMatrix = camera.combined
        batch {
            chunkManager.visibleChunks.forEach { chunk ->
                font.draw(
                    this, "(${chunk.coords.x}, ${chunk.coords.y})",
                    chunk.bounds.x,
                    chunk.bounds.y + 50f,
                )
            }
        }

    }
}
