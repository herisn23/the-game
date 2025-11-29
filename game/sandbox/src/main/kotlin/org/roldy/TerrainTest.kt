package org.roldy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import org.roldy.terrain.shader.ProceduralTerrainGenerator
import org.roldy.utils.sequencer

class TerrainTest(
    val zoomSensitivity: Float = 1f
) {
    private val tiledMap: TiledMap = ProceduralTerrainGenerator.generateTerrain(100, 100, 200)
    private val mapRenderer = OrthogonalTiledMapRenderer(tiledMap)


    val layersVisibility by sequencer {
        listOf(
            intArrayOf(0, 1),
            intArrayOf(1),
            intArrayOf(0)
        )
    }

    context(_: Float, camera: OrthographicCamera)
    fun render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            layersVisibility.next()
        }
        // Handle input
        handleInput()
        mapRenderer.setView(camera)

//        // Enable blending BEFORE any rendering
//        Gdx.gl.glEnable(GL20.GL_BLEND)
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        mapRenderer.render()
    }

    context(delta: Float, camera: OrthographicCamera)
    private fun handleInput() {

        // Zoom
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom += zoomSensitivity * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom -= zoomSensitivity * delta
        }
        camera.zoom = MathUtils.clamp(camera.zoom, 1f, 50f)
    }

    fun dispose() {
        tiledMap.dispose()
    }
}