package org.roldy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import org.roldy.terrain.ProceduralMapGenerator
import org.roldy.terrain.TerrainDebugRenderer
import org.roldy.utils.sequencer

class TerrainTest(
    val zoomSensitivity: Float = 1f
) {

    // Map parameters
    private val width = 100
    private val height = 100
    private val tileSize = 200

    // Create procedural generator (this is safe during init, no OpenGL calls)
    private val generator = ProceduralMapGenerator(
        seed = 1,
        width = width,
        height = height,
        tileSize = tileSize,
        elevationScale = 0.001f,    // Lower = smoother elevation changes
        moistureScale = 0.003f,      // Lower = larger moisture zones
        temperatureScale = 0.05f,    // Lower = larger temperature zones
        enableTransitions = true,    // Enable transitions
        debugMode = false             // Enable debug mode
    )
    val tiledMap = generator.generate()
    val tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
    private val debugInfo = generator.getAllDebugInfo()

    // Debug renderer
    private val debugRenderer = TerrainDebugRenderer(tileSize)
    private var debugEnabled = false
    private var debugTogglePressed = false

    val sequencer by sequencer {
        listOf(
            intArrayOf(0, 1),  //only biome and its transitions
            intArrayOf(0), //only base layer,
//            intArrayOf(2) //only biome colors
        )
    }

    context(_: Float, camera: OrthographicCamera)
    fun render() {
        // Handle input
        handleInput()
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render(sequencer.current)

        // Render debug overlay if enabled
        if (debugEnabled) {
            debugRenderer.render(camera, debugInfo)
        }
    }

    context(delta: Float, camera: OrthographicCamera)
    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            sequencer.next()
        }
        // Zoom
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom += zoomSensitivity * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom -= zoomSensitivity * delta
        }
        // Adjusted zoom range for 200×200 tile map
        camera.zoom = MathUtils.clamp(camera.zoom, 3f, 10f)

        // Toggle debug overlay with D key
        val debugKeyPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE)
        if (debugKeyPressed && !debugTogglePressed) {
            debugEnabled = !debugEnabled
            println("Debug overlay: ${if (debugEnabled) "enabled" else "disabled"}")
        }
        debugTogglePressed = debugKeyPressed
    }

    fun dispose() {
        tiledMapRenderer.dispose()
        debugRenderer.dispose()
    }
}