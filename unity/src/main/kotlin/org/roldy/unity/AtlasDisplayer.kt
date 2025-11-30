package org.roldy.unity

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas

val path = "terrain/Water.atlas"

fun main() {
    Lwjgl3Application(AtlasViewerScrollable(), Lwjgl3ApplicationConfiguration().apply {
        setWindowedMode(1024, 768)
    })

}

class AtlasViewerScrollable : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var atlas: TextureAtlas
    private lateinit var font: BitmapFont
    private lateinit var camera: OrthographicCamera

    private lateinit var regions: com.badlogic.gdx.utils.Array<TextureAtlas.AtlasRegion>
    private val columns = 5
    private val regionSpacing = 20f
    private val regionSize = 128f
    private var scrollY = 0f
    private val scrollSpeed = 300f

    override fun create() {
        batch = SpriteBatch()
        atlas = TextureAtlas(Gdx.files.internal(path))
        regions = atlas.regions

        font = BitmapFont()
        font.data.setScale(1.2f)

        camera = OrthographicCamera()
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        println("Loaded ${regions.size} regions from atlas")
    }

    override fun render() {
        // Handle scrolling
        val delta = Gdx.graphics.deltaTime
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            scrollY += scrollSpeed * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            scrollY -= scrollSpeed * delta
        }

        // Mouse wheel scrolling
        scrollY += Gdx.input.deltaY * 20f

        // Clamp scroll
        val maxScroll = calculateMaxScroll()
        scrollY = scrollY.coerceIn(0f, maxScroll)

        // Clear screen
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined

        batch.begin()

        val startX = 50f
        val startY = Gdx.graphics.height - regionSize - 50f + scrollY

        // Render each region with its name
        regions.forEachIndexed { i, region ->
            val row = i / columns
            val col = i % columns

            val x = startX + col * (regionSize + regionSpacing)
            val y = startY - row * (regionSize + regionSpacing + 40)

            // Only draw if visible on screen
            if (y + regionSize > 0 && y < Gdx.graphics.height) {
                // Draw the texture region
                batch.draw(region, x, y, regionSize, regionSize)

                // Draw the name below the region
                font.draw(batch, region.name, x, y - 10)

                // Optional: Draw index number
                font.draw(batch, "#$i", x, y + regionSize + 15)
            }
        }

        // Draw info text
        font.draw(batch, "Regions: ${regions.size} | Use arrow keys or mouse wheel to scroll", 10f, 30f)

        batch.end()
    }

    private fun calculateMaxScroll(): Float {
        val rows = (regions.size + columns - 1) / columns
        val totalHeight = rows * (regionSize + regionSpacing + 40)
        return (totalHeight - Gdx.graphics.height + 100f).coerceAtLeast(0f)
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    override fun dispose() {
        batch.dispose()
        atlas.dispose()
        font.dispose()
    }
}