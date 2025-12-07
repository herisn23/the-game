package org.roldy.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import org.roldy.map.input.WorldMapInputProcessor
import org.roldy.terrain.ProceduralMapGenerator


class WorldMap(
    // Map parameters
    private val camera: OrthographicCamera,
    private val inputProcessor: WorldMapInputProcessor,
    generator: ProceduralMapGenerator
) {
    val terrainData = generator.terrainData
    private val tiledMap = generator.generate("biomes-configuration-hex.yaml").apply {
        properties.put("width", generator.width)
        properties.put("height", generator.height)
        properties.put("tilewidth", generator.tileSize)
        properties.put("tileheight", generator.tileSize)
        properties.put("hexsidelength", generator.tileSize / 2)
        properties.put("staggeraxis", "y") // or "x"
        properties.put("staggerindex", "even"); // or "odd"
    }

    private val tiledMapRenderer = HexagonalTiledMapRenderer(tiledMap)
    val tileFocus = TileFocus(tiledMap, terrainData)
    context(_: Float)
    fun render() {
        // Handle input
        zoomCamera()
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()//IntArray(1){0}
        tileFocus.render(camera)
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