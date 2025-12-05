package org.roldy.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import org.roldy.keybind.keybinds
import org.roldy.map.input.WorldMapInputProcessor
import org.roldy.terrain.ProceduralMapGenerator

interface WorldMapSize {
    val width: Int
    val height: Int

    object Small : WorldMapSize {
        override val width: Int = 300
        override val height: Int = 300
    }

    object Medium : WorldMapSize {
        override val width: Int = 600
        override val height: Int = 600
    }

    object Large : WorldMapSize {
        override val width: Int = 1000
        override val height: Int = 1000
    }

    object ExtraLarge : WorldMapSize {
        override val width: Int = 2000
        override val height: Int = 2000
    }

}

class WorldMap(
    // Map parameters
    size: WorldMapSize,
    seed: Long,
    private val camera: OrthographicCamera,
    generator: ProceduralMapGenerator = ProceduralMapGenerator(
        seed = seed,
        width = size.width,
        height = size.height,
        tileSize = 200,
        elevationScale = 0.001f,    // Lower = smoother elevation changes
        moistureScale = 0.003f,      // Lower = larger moisture zones
        temperatureScale = 0.05f,    // Lower = larger temperature zones
        enableTransitions = true,    // Enable transitions
        debugMode = false             // Enable debug mode
    )
) {

    val terrainData = generator.terrainData
    private val tiledMap = generator.generate()
    private val tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
    val inputProcessor = WorldMapInputProcessor(keybinds)

    context(_: Float)
    fun render() {
        // Handle input
        zoomCamera()
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()
    }

    context(delta: Float)
    private fun zoomCamera() {
        inputProcessor.zoom { zoom ->
            camera.zoom += zoom * delta
            camera.zoom = MathUtils.clamp(camera.zoom, 3f, 10f)
        }
    }

    fun dispose() {
        tiledMapRenderer.dispose()
    }
}