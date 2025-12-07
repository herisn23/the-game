package org.roldy.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import org.roldy.core.logger
import org.roldy.core.utils.sequencer
import org.roldy.core.x
import org.roldy.map.input.WorldMapInputProcessor
import org.roldy.terrain.ProceduralMapGenerator
import kotlin.math.floor


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
//        properties.put("hexsidelength", 126)
        properties.put("staggeraxis", "y") // or "x"
        properties.put("staggerindex", "even"); // or "odd"
    }

    private val tiledMapRenderer = HexagonalTiledMapRenderer(tiledMap)

    val layers = sequencer(
        listOf(
            IntArray(1){0},
            IntArray(1){1}
        )
    )

    context(_: Float)
    fun render() {
        // Handle input
        zoomCamera()
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render(layers.current)

        checkTile()
    }

    context(delta: Float)
    private fun zoomCamera() {
        inputProcessor.zoom { zoom ->
            camera.zoom += zoom * delta
            camera.zoom = MathUtils.clamp(camera.zoom, 3f, 50f)
        }
    }

    fun dispose() {
        tiledMapRenderer.dispose()
    }

    fun checkTile() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            layers.next()
        }

        if (Gdx.input.justTouched()) {
            val mousePos = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(mousePos)

            val layer = tiledMap.layers[0] as TiledMapTileLayer

            val hexSideLength = tiledMap.properties.get("hexsidelength", Int::class.java) ?: 126
            val staggerIndex = tiledMap.properties.get("staggerindex", String::class.java) ?: "even"

            // For flat-top hexagons
            val rowHeight = hexSideLength + (layer.tileHeight - hexSideLength) / 2f
            val tileY = (mousePos.y / rowHeight).toInt()

            // Check if this row is staggered
            val isStaggered = (tileY % 2 == 0 && staggerIndex == "even") ||
                    (tileY % 2 == 1 && staggerIndex == "odd")
            val offsetX = if (isStaggered) layer.tileWidth * 0.5f else 0f

            // Add small epsilon to avoid rounding issues at tile boundaries
            val tileX = floor((mousePos.x - offsetX) / layer.tileWidth).toInt()

            println("Calculated tile: $tileX, $tileY")

            // 5. Check bounds and get tile
            if (tileX in 0 until layer.width && tileY in 0 until layer.height) {
                println("Map width: ${layer.width}")
                println("Map height: ${layer.height}")
                println("Tile width: ${layer.tileWidth}")
                println("Tile height: ${layer.tileHeight}")
                println("Stagger axis: ${tiledMap.properties.get("staggeraxis")}")
                println("Stagger index: ${tiledMap.properties.get("staggerindex")}")
                println("Hex side length: ${tiledMap.properties.get("hexsidelength")}")
                println("Orientation: ${tiledMap.properties.get("orientation")}")

// When clicking, print these values:
                println("Mouse screen: ${Gdx.input.x}, ${Gdx.input.y}")
                println("Mouse world: ${mousePos.x}, ${mousePos.y}")
                println("Calculated tile: $tileX, $tileY")
                val data = terrainData[tileX x tileY]
                logger.debug {
                    "${data?.noiseData}"
                }
                val cell = layer.getCell(tileX, tileY)
                logger.debug {
                    "${cell?.tile?.textureRegion}"
                }
            }
        }
    }
}