package org.roldy.rendering.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.data.map.MapData
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter


class WorldMap(
    // Map parameters
    private val camera: OrthographicCamera,
    val data: MapData,
    val tiledMap: TiledMap,
    val terrainData: Map<Vector2Int, TileData>
) : AutoDisposableAdapter() {

    val staggerAxis = "y"
    val staggerIndex = "even"
    val tileWidth = data.tileSize
    val tileHeight = data.tileSize
    val width: Int = data.size.width
    val height: Int = data.size.height
    val hexSideLength = data.tileSize / 2
    var zoom: Float = 0f

    init {
        tiledMap.apply {
            properties.put("width", width)
            properties.put("height", height)
            properties.put("tilewidth", tileWidth)
            properties.put("tileheight", tileHeight)
            properties.put("hexsidelength", hexSideLength)
            properties.put("staggeraxis", staggerAxis) // or "x"
            properties.put("staggerindex", staggerIndex); // or "odd"
        }
    }

    val yCorrection = .75f
    private val tiledMapRenderer = HexagonalTiledMapRenderer(tiledMap).disposable()
    val tilePosition = TilePositionResolver(this)
    private val tileFocus = TileFocus(tilePosition).disposable()

    val mapBounds = MapBounds(tiledMap)

    context(_: Float)
    fun render() {
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()//IntArray(1){0}
        tileFocus.render(camera)
    }

    fun clampToBounds(position: Vector2, newPosition: (Vector2) -> Unit) {
        if (!mapBounds.isInBounds(position)) {
            newPosition(mapBounds.clampToMap(position))
        }
    }
}

