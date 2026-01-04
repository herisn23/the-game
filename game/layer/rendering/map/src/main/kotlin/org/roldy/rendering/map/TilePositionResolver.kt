package org.roldy.rendering.map

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.x

class TilePositionResolver(
    val worldMap: WorldMap
) {
    val logger by logger()
    val layer = worldMap.tiledMap.layers[1] as TiledMapTileLayer
    val hexSideLength: Int = worldMap.hexSideLength
    val staggerAxis: String = worldMap.staggerAxis
    val staggerIndex: String = worldMap.staggerIndex
    private val tileWidth = layer.tileWidth.toFloat()
    private val tileHeight = layer.tileHeight.toFloat()

    init {
        logger.info("TilePositionResolver initialized:")
        logger.info("  tileWidth=$tileWidth, tileHeight=$tileHeight")
        logger.info("  hexSideLength=$hexSideLength")
        logger.info("  staggerIndex=$staggerIndex")
    }

    fun center(x: Float, y: Float) =
        (x + tileWidth / 2) x (y + tileHeight / 2)

    fun center(position: Vector2) =
        center(position.x, position.y)

    @JvmName("centerExt")
    fun Vector2.center() =
        center(x, y)

    private fun getTileWorldPosition(col: Int, row: Int): Vector2 {
        val x: Float
        val y: Float

        // Y-axis stagger (flat-top hexagons)
        val hexWidth = tileWidth
        val hexHeight = tileHeight * 0.75f  // Rows are 75% of height apart, not 100%

        // Get tile origin
        val originX = col * hexWidth + if (worldMap.isStaggered(row)) hexWidth / 2 else 0f
        val originY = row * hexHeight

        // Add half width/height to get CENTER
        x = originX
        y = originY

        return x x y
    }

    /**
     * Convert world position to tile coordination
     */
    operator fun invoke(
        position: Vector2,
        onMiss: () -> Unit = {},
        onMatch: (Vector2Int, Vector2, MapTerrainData) -> Unit
    ) {
        resolve(position, onMiss, onMatch)
    }

    /**
     * Convert world position to tile coordination
     */
    fun resolve(
        position: Vector2,
        onMiss: () -> Unit = {},
        onMatch: (Vector2Int, Vector2, MapTerrainData) -> Unit
    ) {
        resolve(position)?.let {
            onMatch(it, getTileWorldPosition(it.x, it.y), worldMap.terrainData.getValue(it))
        } ?: onMiss()
    }

    /**
     * Convert world position to tile coordination
     */
    /**
     * Convert world position to tile coordination
     */
    fun resolve(position: Vector2): Vector2Int? {
        val mousePos = Vector3(position.x, position.y, 0f)

        if (staggerAxis == "y") {
            // Flat-top hexagons (Y-axis stagger)
            val hexHeight = tileHeight * 0.75f  // Match the spacing used in getTileWorldPosition
            val tileY = (mousePos.y / hexHeight).toInt()

            val staggered = worldMap.isStaggered(tileY)
            val offsetX = if (staggered) tileWidth * 0.5f else 0f
            val tileX = ((mousePos.x - offsetX) / tileWidth).toInt()

            return if (tileX in 0 until layer.width && tileY in 0 until layer.height) {
                tileX x tileY
            } else {
                null
            }
        } else {
            // Pointy-top hexagons (X-axis stagger)
            val hexWidth = tileWidth * 0.75f  // Match the spacing used in getTileWorldPosition
            val tileX = (mousePos.x / hexWidth).toInt()

            val staggered = worldMap.isStaggered(tileX)
            val offsetY = if (staggered) tileHeight * 0.5f else 0f
            val tileY = ((mousePos.y - offsetY) / tileHeight).toInt()

            return if (tileX in 0 until layer.width && tileY in 0 until layer.height) {
                tileX x tileY
            } else {
                null
            }
        }
    }

    /**
     * Convert tile coords to world position
     */
    fun resolve(tile: Vector2Int): Vector2 {
        return getTileWorldPosition(tile.x, tile.y)
    }

    /**
     * Convert tile coords to world position
     */
    operator fun invoke(
        position: Vector2Int,
        converted: (Vector2Int, Vector2, MapTerrainData) -> Unit
    ) {
        converted(position, resolve(position), worldMap.terrainData.getValue(position))
    }

}