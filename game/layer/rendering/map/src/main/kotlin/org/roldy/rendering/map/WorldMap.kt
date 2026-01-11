package org.roldy.rendering.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.data.map.MapData
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter


class WorldMap(
    // Map parameters
    val data: MapData,
    val tiledMap: TiledMap,
    val outlines: TextureAtlas,
    val terrainData: Map<Vector2Int, MapTerrainData>,
    private val biomeShowColorsInsteadTextures: Boolean = false
) : AutoDisposableAdapter() {
    val layer = tiledMap.layers[0] as TiledMapTileLayer
    val staggerAxis = "y"
    val staggerIndex = if (data.size.height % 2 == 1) "odd" else "even"
    val heightIsEven = data.size.height % 2 == 1
    val tileWidth = layer.tileWidth
    val tileHeight = layer.tileHeight
    val width: Int = data.size.width
    val height: Int = data.size.height
    val hexSideLength = tileHeight / 2
    val viewPortWidth = data.size.viewPortWidth(tileWidth)
    val viewPortHeight = data.size.viewPortHeight(tileHeight)

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
//        val layer = tiledMap.layers.
        println("Map size: ${data.size.width} x ${data.size.height}")
//        println("Layer size: ${layer.width} x ${layer.height}")
        println("Tile size: $tileWidth x $tileHeight")
    }


    fun isStaggered(coords: Vector2Int): Boolean {
        val index = if (staggerAxis == "y") coords.y else coords.x
        // Y-axis stagger (flat-top hexagons)
        return isStaggered(index)
    }

    fun isStaggered(index: Int): Boolean {
        return if (staggerIndex == "even" && heightIsEven) {
            index % 2 == 0  // Even rows (0, 2, 4...) are staggered
        } else {
            index % 2 == 1  // Odd rows (1, 3, 5...) are staggered
        }
    }

    private val tiledMapRenderer = HexagonalTiledMapRenderer(tiledMap).disposable()
    val tilePosition = TilePositionResolver(this)
    val mapBounds = MapBounds(this)

    private val tileFocus = TileFocus(tilePosition, outlines).disposable()

    fun render(camera: OrthographicCamera) {
        tiledMapRenderer.setView(camera)
        if (biomeShowColorsInsteadTextures) {
            tiledMapRenderer.render(IntArray(1) { tiledMap.layers.size() - 1 })
        } else {
            tiledMapRenderer.render()//IntArray(1){0}
        }

        tileFocus.render(camera)
    }

    fun clampToBounds(position: Vector2, newPosition: (Vector2) -> Unit) {
        if (!mapBounds.isInBounds(position)) {
            newPosition(mapBounds.clampToMap(position))
        }
    }
}

