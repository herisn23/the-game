package org.roldy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import org.roldy.terrain.ProceduralMapGenerator

class TerrainTest(
    val zoomSensitivity: Float = 1f
) {

    // Create generator
    val generator = ProceduralMapGenerator(
        seed = 1,
        width = 1000,
        height = 1000,
        tileSize = 400,
        elevationScale = 0.001f,    // Lower = smoother elevation changes
        moistureScale = 0.003f,      // Lower = larger moisture zones
        temperatureScale = 0.05f,    // Lower = larger temperature zones
        enableTransitions = true
    )

    // Generate the map
    val tiledMap = generator.generate()

    // Create renderer
    val renderer = OrthogonalTiledMapRenderer(tiledMap)

//    val w = 100
//    val h = 100
//    val splatmapGenerator = SplatMapGenerator(w, h, noiseMap)
//    val splatmaps = splatmapGenerator.generateSplatmaps()
//    val tr = TerrainRenderer(w, h, 400, splatmaps)
    context(_: Float, camera: OrthographicCamera)
    fun render() {
        // Handle input
        handleInput()
//        tr.render(camera)
        renderer.setView(camera)
//        // Enable blending BEFORE any rendering
//        Gdx.gl.glEnable(GL20.GL_BLEND)
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        renderer.render()
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
        camera.zoom = MathUtils.clamp(camera.zoom, 3f, 50f)
    }

    fun dispose() {
        renderer.dispose()
        tiledMap.dispose()
    }
}