package org.roldy.rendering.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.invoke
import org.roldy.core.x
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable

class WorldMapDebugRenderer(
    private val worldMap: WorldMap,
    private val camera: OrthographicCamera
) : AutoDisposableAdapter() {

    private val batch by disposable { SpriteBatch() }
    private val font by disposable {
        BitmapFont().apply {
            data.setScale(4f)
            color = Color.RED
        }
    }
    private val shapeRenderer by disposable { ShapeRenderer() }

    private val tiledMap: TiledMap = worldMap.tiledMap
    fun render() {
        val visibleTiles = getVisibleTiles()

        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        visibleTiles.forEach { (col, row) ->
            val center = getHexCenter(col, row)
            val text = "$col, $row"

            // Center the text
//            val layout = font.draw(batch, text, 0f, 0f)
//            val textWidth = layout.width
//            val textHeight = layout.height

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = Color.GREEN
            shapeRenderer.circle(center.x, center.y, 5f)
            shapeRenderer.end()

            batch {
                font.draw(
                    batch,
                    text,
                    center.x,
                    center.y
                )
            }
        }
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
        return worldMap.tilePosition.run {
            resolve(col x row).center()
        }
    }
}