package org.roldy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import org.roldy.terrain.shader.ProceduralTerrainGenerator

class TerrainTest(
    val zoomSensitivity:Float = 1f
) {
    private val tiledMap: TiledMap by lazy {
        ProceduralTerrainGenerator.generateTerrain(1000, 1000)
    }
    private val mapRenderer: OrthogonalTiledMapRenderer by lazy {
        OrthogonalTiledMapRenderer(tiledMap, 1f)
    }

    context(delta: Float, camera: OrthographicCamera)
    fun render() {
        // Handle input
        handleInput()


        mapRenderer.setView(camera)
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
        camera.zoom = MathUtils.clamp(camera.zoom, 5f, 20f)
    }

    fun dispose() {
        tiledMap.dispose()
    }
}