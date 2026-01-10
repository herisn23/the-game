package org.roldy.rendering.map

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import kotlin.math.floor

class MapBounds(private val map: WorldMap) {
    private val layer = map.layer
    private val hexSideLength = map.tiledMap.properties.get("hexsidelength", Int::class.java).toFloat()
    private val staggerIndex = map.tiledMap.properties.get("staggerindex", String::class.java)

    private val tileWidth = layer.tileWidth.toFloat()
    private val tileHeight = layer.tileHeight.toFloat()
    private val rowHeight = (tileHeight + hexSideLength) / 2f

    val mapWidth = layer.width
    val mapHeight = layer.height

    // Check if tile coordinates are in bounds
    fun isInBounds(tileX: Int, tileY: Int): Boolean {
        return tileX in 0 until mapWidth && tileY in 0 until mapHeight
    }

    fun isInBounds(tilePos: Vector2Int): Boolean {
        return isInBounds(tilePos.x, tilePos.y)
    }

    // Check if world coordinates are in bounds
    fun isInBounds(worldX: Float, worldY: Float): Boolean {
        val tilePos = worldToTile(worldX, worldY)
        return isInBounds(tilePos.x, tilePos.y)
    }

    fun isInBounds(worldPos: Vector2): Boolean {
        return isInBounds(worldPos.x, worldPos.y)
    }

    // Convert world coordinates to tile coordinates
    fun worldToTile(worldX: Float, worldY: Float): Vector2Int {
        val tileY = (worldY / rowHeight).toInt()

        val staggered = if (staggerIndex == "even") {
            tileY % 2 == 1
        } else {
            tileY % 2 == 0
        }

        val offsetX = if (staggered) tileWidth * 0.5f else 0f
        val tileX = floor((worldX - offsetX) / tileWidth).toInt()

        return Vector2Int(tileX, tileY)
    }

    // Get world bounds (useful for camera clamping)
    fun getWorldBounds(): Rectangle {
        // Calculate total world size
        val maxX = mapWidth * tileWidth + tileWidth * 0.5f  // Account for stagger
        val maxY = mapHeight * rowHeight + tileHeight * 0.5f  // Account for hex height

        return Rectangle(0f, 0f, maxX, maxY)
    }

    // Clamp position to map bounds
    fun clampToMap(worldPos: Vector2): Vector2 {
        val bounds = getWorldBounds()
        return Vector2(
            worldPos.x.coerceIn(0f, bounds.width),
            worldPos.y.coerceIn(0f, bounds.height)
        )
    }

    // Check if position is near edge (useful for chunk loading triggers)
    fun isNearEdge(worldPos: Vector2, edgeDistance: Float = 256f): Boolean {
        val bounds = getWorldBounds()
        return worldPos.x < edgeDistance ||
                worldPos.y < edgeDistance ||
                worldPos.x > bounds.width - edgeDistance ||
                worldPos.y > bounds.height - edgeDistance
    }
}