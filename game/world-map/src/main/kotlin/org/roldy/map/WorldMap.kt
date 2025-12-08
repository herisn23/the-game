package org.roldy.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.map.input.ZoomCameraProcessor
import org.roldy.terrain.ProceduralMapGenerator


class WorldMap(
    // Map parameters
    private val camera: OrthographicCamera,
    private val zoom: ZoomCameraProcessor,
    val seed: Long,
    val mapSize: WorldMapSize,
    val tileSize: Int
) : AutoDisposableAdapter() {

    val generator = ProceduralMapGenerator(
        seed = seed,
        width = mapSize.size,
        height = mapSize.size,
        tileSize = tileSize,
        moistureScale = mapSize.moistureScale,
        temperatureScale = mapSize.temperatureScale,
        elevationScale = mapSize.elevationScale
    ).disposable()
    val terrainData = generator.terrainData

    val staggerAxis = "y"
    val staggerIndex = "even"
    val tileWidth = generator.tileSize
    val tileHeight = generator.tileSize
    val width: Int = generator.width
    val height: Int = generator.height
    val hexSideLength =  generator.tileSize / 2

    val yCorrection = .75f
    val tiledMap = generator.generate("biomes-configuration-hex.yaml").apply {
        properties.put("width", width)
        properties.put("height", height)
        properties.put("tilewidth", tileWidth)
        properties.put("tileheight", tileHeight)
        properties.put("hexsidelength", hexSideLength)
        properties.put("staggeraxis", staggerAxis) // or "x"
        properties.put("staggerindex", staggerIndex); // or "odd"
    }

    private val tiledMapRenderer = HexagonalTiledMapRenderer(tiledMap).disposable()
    val tilePosition = TilePositionResolver(this, terrainData)
    private val tileFocus = TileFocus(tilePosition).disposable()

    val mapBounds = MapBounds(tiledMap)

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
        zoom { zoom ->
            camera.zoom += zoom * delta
            camera.zoom = MathUtils.clamp(camera.zoom, 2f, 10f)
        }
    }

    fun clampToBounds(position: Vector2, newPosition: (Vector2) -> Unit) {
        if (!mapBounds.isInBounds(position)) {
            newPosition(mapBounds.clampToMap(position))
        }
    }
}

