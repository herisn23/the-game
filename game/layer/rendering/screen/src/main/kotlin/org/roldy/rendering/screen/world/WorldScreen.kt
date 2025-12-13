package org.roldy.rendering.screen.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import org.roldy.rendering.g2d.Diagnostics
import org.roldy.rendering.g2d.chunk.ChunkRenderer
import org.roldy.rendering.g2d.disposable.AutoDisposableScreenAdapter
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunkManager
import org.roldy.rendering.screen.world.debug.DebugRenderer
import org.roldy.rendering.screen.world.populator.WorldMapPopulator

class WorldScreen(
    private val camera: OrthographicCamera,
    private val map: WorldMap,
    populator: WorldMapPopulator,
    val inputProcessor: InputProcessor,
    val zoom: ((Float, Float, Float) -> Unit) -> Unit,
    private val debugEnabled: Boolean = false
) : AutoDisposableScreenAdapter() {
    val batch by disposable { SpriteBatch() }
    val diagnostics by disposable { Diagnostics() }

    val chunkManager by disposable {
        WorldMapChunkManager(
            map,
            populator
        )
    }

    val chunkRenderer = ChunkRenderer(
        camera,
        chunkManager
    )

    val debugRenderer by disposable { DebugRenderer(camera, map, chunkManager) }

    override fun resize(width: Int, height: Int) {
        diagnostics.resize(width, height)
    }

    override fun render(delta: Float) {
        context(delta) {
            zoom { zoom, min, max ->
                camera.zoom += zoom * delta
                camera.zoom = MathUtils.clamp(camera.zoom, min, max)
            }
            camera.update()
            map.render()
            chunkRenderer.render(batch)
            if (debugEnabled) {
                debugRenderer.render()
            }
            diagnostics.render()
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = inputProcessor
        chunkManager.pause = false
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
        chunkManager.pause = true
    }

}