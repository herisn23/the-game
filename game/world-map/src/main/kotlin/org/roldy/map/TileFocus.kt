package org.roldy.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable
import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.x
import org.roldy.terrain.TileData
import kotlin.math.floor

class TileFocus(
    private val tiledMap: TiledMap,
    private val terrainData: Map<Vector2Int, TileData>,
    private val onTileFocus: (TileData) -> Unit = {}
) : Disposable {
    val layer = tiledMap.layers[0] as TiledMapTileLayer
    val hexSideLength: Int = tiledMap.properties.get("hexsidelength", Int::class.java)
    val staggerIndex: String = tiledMap.properties.get("staggerindex", String::class.java)
    val highlightTexture = Texture("HexTileHighlighter.png")
    private val batch = SpriteBatch()
    var focusedTile: Vector2Int? = null
    var focusTilePosition: Vector2? = null

    private val tileWidth = layer.tileWidth.toFloat()
    private val tileHeight = layer.tileHeight.toFloat()
    private val hexSideLengthF = hexSideLength.toFloat()
    private val rowHeight = (tileHeight + hexSideLengthF) / 2f

    init {
        logger.debug("HexHighlighter initialized:")
        logger.debug("  tileWidth=$tileWidth, tileHeight=$tileHeight")
        logger.debug("  hexSideLength=$hexSideLength")
        logger.debug("  rowHeight=$rowHeight")
        logger.debug("  staggerIndex=$staggerIndex")
    }

    fun render(camera: OrthographicCamera) {
        handleHexClick(camera)
        renderFocusTile(camera)
    }

    fun renderFocusTile(camera: OrthographicCamera) {
        focusTilePosition?.run {
            batch.projectionMatrix = camera.combined
            batch.begin()
            batch.draw(highlightTexture, x, y)
            batch.end()
        }
    }

    private fun getTileRenderPosition(tileX: Int, tileY: Int): Vector2 {
        // INVERTED: staggerIndex="even" means ODD rows are staggered
        val staggered = if (staggerIndex == "even") {
            tileY % 2 == 1  // Changed from == 0
        } else {
            tileY % 2 == 0  // Changed from == 1
        }

        val x = tileX * tileWidth + (if (staggered) tileWidth * 0.5f else 0f)
        val y = tileY * rowHeight

        return Vector2(x, y)
    }

    fun handleHexClick(camera: OrthographicCamera) {
        if (Gdx.input.justTouched()) {
            val mousePos = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(mousePos)

            val tileY = (mousePos.y / rowHeight).toInt()

            // INVERTED: same logic as rendering
            val staggered = if (staggerIndex == "even") {
                tileY % 2 == 1  // Changed from == 0
            } else {
                tileY % 2 == 0  // Changed from == 1
            }

            val offsetX = if (staggered) tileWidth * 0.5f else 0f
            val tileX = floor((mousePos.x - offsetX) / tileWidth).toInt()
            if (tileX in 0 until layer.width && tileY in 0 until layer.height) {
                val tileCoords = tileX x tileY
                val tilePos = getTileRenderPosition(tileX, tileY)
                when {
                    tileCoords == focusedTile -> {
                        focusTilePosition = null
                        focusedTile = null
                    }
                    else -> {
                        focusTilePosition = tilePos
                        focusedTile = tileCoords
                        onTileFocus(terrainData.getValue(tileCoords))
                    }
                }
            } else {
                //unfocus when click away from map
                focusTilePosition = null
                focusedTile = null
            }
        }
    }

    override fun dispose() {
        batch.dispose()
        highlightTexture.dispose()
    }
}