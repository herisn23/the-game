package org.roldy.map

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.x
import org.roldy.terrain.TileData
import kotlin.math.floor

class TilePositionResolver(
    worldMap: WorldMap,
    private val terrainData: Map<Vector2Int, TileData>
) {
    val logger by logger()
    val layer = worldMap.tiledMap.layers[1] as TiledMapTileLayer
    val hexSideLength: Int = worldMap.hexSideLength
    val staggerIndex: String = worldMap.staggerIndex
    private val tileWidth = layer.tileWidth.toFloat()
    private val tileHeight = layer.tileHeight.toFloat()
    private val hexSideLengthF = hexSideLength.toFloat()
    private val rowHeight = (tileHeight + hexSideLengthF) / 2f

    init {
        logger.info("HexHighlighter initialized:")
        logger.info("  tileWidth=$tileWidth, tileHeight=$tileHeight")
        logger.info("  hexSideLength=$hexSideLength")
        logger.info("  rowHeight=$rowHeight")
        logger.info("  staggerIndex=$staggerIndex")
    }

    private fun getTileWorldPosition(tileX: Int, tileY: Int, offsetCorrection: Boolean): Vector2 {
        // INVERTED: staggerIndex="even" means ODD rows are staggered
        val staggered = if (staggerIndex == "even") {
            tileY % 2 == 1  // Changed from == 0
        } else {
            tileY % 2 == 0  // Changed from == 1
        }

        val x =
            tileX * tileWidth + (if (staggered) tileWidth * .5f else 0f) + (if (offsetCorrection) tileWidth * .5f else 0f)
        val y = tileY * rowHeight + (if (offsetCorrection) rowHeight * .65f else 0f)

        return x x y
    }

    /**
     * Convert world position to tile coordination
     */
    operator fun invoke(
        position: Vector2,
        offsetCorrection: Boolean = true,
        onMiss: () -> Unit = {},
        onMatch: (Vector2Int, Vector2, TileData) -> Unit
    ) {
        resolve(position, offsetCorrection, onMiss, onMatch)
    }

    /**
     * Convert world position to tile coordination
     */
    fun resolve(
        position: Vector2,
        offsetCorrection: Boolean = true,
        onMiss: () -> Unit = {},
        onMatch: (Vector2Int, Vector2, TileData) -> Unit
    ) {
        resolve(position)?.let {
            onMatch(it, getTileWorldPosition(it.x, it.y, offsetCorrection), terrainData.getValue(it))
        } ?: onMiss()
    }

    /**
     * Convert world position to tile coordination
     */
    fun resolve(position: Vector2): Vector2Int? {
        val mousePos = Vector3(position.x, position.y, 0f)
        val tileY = (mousePos.y / rowHeight).toInt()

        // INVERTED: same logic as rendering
        val staggered = if (staggerIndex == "even") {
            tileY % 2 == 1  // Changed from == 0
        } else {
            tileY % 2 == 0  // Changed from == 1
        }
        val offsetX = if (staggered) tileWidth * 0.5f else 0f
        val tileX = floor((mousePos.x - offsetX) / tileWidth).toInt()
        return if (tileX in 0 until layer.width && tileY in 0 until layer.height) {
            tileX x tileY
        } else {
            null
        }
    }

    /**
     * Convert tile coords to world position
     */
    fun resolve(tile: Vector2Int, offsetCorrection: Boolean = true): Vector2 {
        return getTileWorldPosition(tile.x, tile.y, offsetCorrection)
    }

    /**
     * Convert tile coords to world position
     */
    operator fun invoke(
        position: Vector2Int,
        offsetCorrection: Boolean = true,
        converted: (Vector2Int, Vector2, TileData) -> Unit
    ) {
        converted(position, resolve(position, offsetCorrection), terrainData.getValue(position))
    }
}