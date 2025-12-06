package org.roldy.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import org.roldy.map.input.WorldMapInputProcessor
import org.roldy.terrain.ProceduralMapGenerator

interface WorldMapSize {
    val size: Int
    val chunks: Int get() = size / 10
    val settlements: Int get() = chunks

    object Debug : WorldMapSize {
        override val size: Int = 50
//        override val chunks: Int = size
    }

    object Small : WorldMapSize {
        override val size: Int = 300
    }

    object Medium : WorldMapSize {
        override val size: Int = 600

    }

    object Large : WorldMapSize {
        override val size: Int = 1000
    }

    object ExtraLarge : WorldMapSize {
        override val size: Int = 2000
    }

}

class WorldMap(
    // Map parameters
    private val camera: OrthographicCamera,
    private val inputProcessor: WorldMapInputProcessor,
    generator: ProceduralMapGenerator
) {

    val terrainData = generator.terrainData
    private val tiledMap = generator.generate()

    private val tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)

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
            camera.zoom = MathUtils.clamp(camera.zoom, 3f, 15f)
        }
    }

    fun dispose() {
        tiledMapRenderer.dispose()
    }
}