package org.roldy.rendering.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import org.roldy.core.x
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable

class WorldMapDebugRenderer(
    private val worldMap: WorldMap,
    private val camera: OrthographicCamera
) : AutoDisposableAdapter() {

    private val batch by disposable { SpriteBatch() }
    private val font by disposable { BitmapFont().apply {
        data.setScale(4f)
        color = Color.RED
    } }

    private val tiledMap: TiledMap = worldMap.tiledMap
    private val tileWidth: Int = worldMap.tileWidth
    private val tileHeight: Int = worldMap.tileHeight
    private val staggerAxis: String = worldMap.staggerAxis
    private val staggerIndex: String = worldMap.staggerIndex
    fun render() {
        val visibleTiles = getVisibleTiles()

        batch.projectionMatrix = camera.combined
        batch.begin()

        visibleTiles.forEach { (col, row) ->
            val center = getHexCenter(col, row)
            val text = "$col, $row"

            // Center the text
            val layout = font.draw(batch, text, 0f, 0f)
            val textWidth = layout.width
            val textHeight = layout.height

            font.draw(
                batch,
                text,
                center.x - textWidth / 2,
                center.y + textHeight / 2
            )
        }

        batch.end()
    }

    private fun getVisibleTiles(): List<Pair<Int, Int>> {
        val tiles = mutableListOf<Pair<Int, Int>>()

        val mapWidth = tiledMap.properties.get("width", Int::class.java)
        val mapHeight = tiledMap.properties.get("height", Int::class.java)

        // Get camera bounds
        val camWidth = camera.viewportWidth * camera.zoom
        val camHeight = camera.viewportHeight * camera.zoom
        val camLeft = camera.position.x - camWidth / 2
        val camRight = camera.position.x + camWidth / 2
        val camBottom = camera.position.y - camHeight / 2
        val camTop = camera.position.y + camHeight / 2

        // Check each tile if it's in view (simplified, might need optimization)
        for (col in 0 until mapWidth) {
            for (row in 0 until mapHeight) {
                val center = getHexCenter(col, row)

                if (center.x >= camLeft && center.x <= camRight &&
                    center.y >= camBottom && center.y <= camTop
                ) {
                    tiles.add(col to row)
                }
            }
        }

        return tiles
    }

    private fun getHexCenter(col: Int, row: Int): Vector2 {
        return worldMap.tilePosition.resolve(col x row)
        val x: Float
        val y: Float

        if (staggerAxis == "y") {
            // Y-axis stagger (flat-top hexagons)
            val hexWidth = tileWidth.toFloat()
            val hexHeight = (tileHeight * worldMap.yCorrection)

            // Get tile origin
            val originX = col * hexWidth + if (isStaggered(row)) hexWidth / 2 else 0f
            val originY = row * hexHeight

            // Add half width/height to get CENTER
            x = originX + hexWidth / 2
            y = originY + hexHeight / 2
        } else {
            // X-axis stagger (pointy-top hexagons)
            val hexWidth = (tileWidth * worldMap.yCorrection)
            val hexHeight = tileHeight.toFloat()

            val originX = col * hexWidth
            val originY = row * hexHeight + if (isStaggered(col)) hexHeight / 2 else 0f

            // Add half width/height to get CENTER
            x = originX + hexWidth / 2
            y = originY + hexHeight / 2
        }

        return Vector2(x, y)
    }

    private fun isStaggered(index: Int): Boolean {
        return if (staggerIndex == "even") {
            index % 2 == 0
        } else {
            index % 2 == 1
        }
    }
}