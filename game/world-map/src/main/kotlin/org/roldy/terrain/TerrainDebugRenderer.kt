package org.roldy.terrain

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Disposable

/**
 * Renders debug information over terrain tiles
 * Shows tile positions and terrain names
 *
 * @param tileSize Size of each tile in pixels
 * @param font BitmapFont to use for rendering text (optional, creates default if null)
 */
class TerrainDebugRenderer(
    private val tileSize: Int
) : Disposable {

    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val debugFont = BitmapFont().apply {
        data.setScale(2f)
        color = Color.RED
    }

    private var enabled = true
    private var showPosition = true
    private var showTerrainName = true
    private var showBiomeName = false
    private var backgroundColor = Color(0f, 0f, 0f, 0f)

    /**
     * Renders debug information for all tiles
     *
     * @param camera The camera used for rendering
     * @param debugInfo List of debug information for each tile
     */
    fun render(camera: OrthographicCamera, debugInfo: List<ProceduralMapGenerator.DebugInfo>) {

        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // Calculate visible tile range based on camera viewport
        val visibleTiles = getVisibleTileRange(camera)

        debugInfo.forEach { info ->
            // Only render tiles that are visible
            if (info.x in visibleTiles.minX..visibleTiles.maxX &&
                info.y in visibleTiles.minY..visibleTiles.maxY) {
                renderTileDebugInfo(info)
            }
        }
    }

    /**
     * Renders debug information for a single tile
     */
    private fun renderTileDebugInfo(info: ProceduralMapGenerator.DebugInfo) {
        val worldX = info.x * tileSize.toFloat()
        val worldY = info.y * tileSize.toFloat()

        // Draw semi-transparent background
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
//        shapeRenderer.color = backgroundColor
//        shapeRenderer.rect(worldX, worldY, tileSize.toFloat(), tileSize.toFloat())
//        shapeRenderer.end()

        // Build debug text
        val lines = mutableListOf<String>()
        if (showPosition) {
            lines.add("(${info.x},${info.y})")
        }
        if (showTerrainName) {
            lines.add(info.terrainName)
        }
        if (showBiomeName) {
            lines.add(info.biomeName)
        }

        // Draw text centered on tile
        batch.begin()
        val lineHeight = debugFont.lineHeight
        val totalHeight = lines.size * lineHeight
        var currentY = worldY + (tileSize + totalHeight) / 2

        lines.forEach { line ->
            val layout = debugFont.draw(batch, line, 0f, 0f)
            val textWidth = layout.width
            debugFont.draw(
                batch,
                line,
                worldX + (tileSize - textWidth) / 2,
                currentY
            )
            currentY -= lineHeight
        }
        batch.end()
    }

    /**
     * Calculates which tiles are visible based on camera position and zoom
     */
    private fun getVisibleTileRange(camera: OrthographicCamera): VisibleTileRange {
        val halfWidth = camera.viewportWidth * camera.zoom / 2
        val halfHeight = camera.viewportHeight * camera.zoom / 2

        val minWorldX = camera.position.x - halfWidth
        val maxWorldX = camera.position.x + halfWidth
        val minWorldY = camera.position.y - halfHeight
        val maxWorldY = camera.position.y + halfHeight

        return VisibleTileRange(
            minX = (minWorldX / tileSize).toInt().coerceAtLeast(0),
            maxX = (maxWorldX / tileSize).toInt(),
            minY = (minWorldY / tileSize).toInt().coerceAtLeast(0),
            maxY = (maxWorldY / tileSize).toInt()
        )
    }

    /**
     * Sets whether debug rendering is enabled
     */
    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    /**
     * Sets which debug information to display
     */
    fun setDisplayOptions(
        showPosition: Boolean = this.showPosition,
        showTerrainName: Boolean = this.showTerrainName,
        showBiomeName: Boolean = this.showBiomeName
    ) {
        this.showPosition = showPosition
        this.showTerrainName = showTerrainName
        this.showBiomeName = showBiomeName
    }

    /**
     * Sets the background color for debug overlays
     */
    fun setBackgroundColor(color: Color) {
        this.backgroundColor.set(color)
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        debugFont.dispose()
    }

    private data class VisibleTileRange(
        val minX: Int,
        val maxX: Int,
        val minY: Int,
        val maxY: Int
    )
}