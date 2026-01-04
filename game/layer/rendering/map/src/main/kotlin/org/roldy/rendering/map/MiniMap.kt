package org.roldy.rendering.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable

class MiniMap(
    val map: WorldMap,
    val camera: OrthographicCamera
) : AutoDisposableAdapter() {
    private val minimapCamera = OrthographicCamera()
    private var minimapFBO: FrameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, (4096), (4096), false)
    private val minimapBatch by disposable { SpriteBatch() }
    private val minimapShapeRenderer by disposable { ShapeRenderer() }
    val mapWidth = map.viewPortWidth
    val mapHeight = map.viewPortHeight

    private val minimapWidth = 250f
    private val minimapHeight = 250f
    var x = 20f
    var y = 100f

    init {
        // Setup minimap camera to view entire map
        minimapCamera.setToOrtho(false, mapWidth, mapHeight)
        minimapCamera.position.set(mapWidth / 2f, mapHeight / 2f, 0f)
        minimapCamera.update()
        preRender()
    }

    private fun preRender() {

        val fboWidth = map.viewPortWidth
        val fboHeight = map.viewPortHeight

        // Update minimap camera viewport to match FBO size
        minimapCamera.viewportWidth = fboWidth
        minimapCamera.viewportHeight = fboHeight
        minimapCamera.position.set(fboWidth / 2f, fboHeight / 2f, 0f)
        minimapCamera.update()

        minimapFBO.begin()

        // Clear minimap
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Render entire map to minimap
        map.render(minimapCamera)
        minimapFBO.end()
    }

    fun render() {
        minimapBatch.begin()
        minimapBatch.draw(
            minimapFBO.colorBufferTexture,
            x, y,
            minimapWidth, minimapHeight,
            0f, 0f, 1f, 1f
        )
        minimapBatch.end()
        drawBounds()
    }

    private fun drawBounds() {
        // Calculate camera position in minimap coordinates
        val camXNormalized = camera.position.x / mapWidth
        val camYNormalized = camera.position.y / mapHeight
        val camMinimapX = x + camXNormalized * minimapWidth
        val camMinimapY = y + camYNormalized * minimapHeight

        // Calculate viewport size in minimap
        val viewWidthNormalized = (camera.viewportWidth * camera.zoom) / mapWidth
        val viewHeightNormalized = (camera.viewportHeight * camera.zoom) / mapHeight
        val viewMinimapWidth = viewWidthNormalized * minimapWidth
        val viewMinimapHeight = viewHeightNormalized * minimapHeight

        // Draw camera view bounds
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        minimapShapeRenderer.color = Color.YELLOW
        minimapShapeRenderer.rect(
            camMinimapX - viewMinimapWidth / 2,
            camMinimapY - viewMinimapHeight / 2,
            viewMinimapWidth,
            viewMinimapHeight
        )
        minimapShapeRenderer.end()

        // Draw camera center point
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        minimapShapeRenderer.color = Color.RED
        minimapShapeRenderer.circle(camMinimapX, camMinimapY, 3f)
        minimapShapeRenderer.end()

        // Draw border around minimap
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        minimapShapeRenderer.color = Color.WHITE
        minimapShapeRenderer.rect(x, y, minimapWidth, minimapHeight)
        minimapShapeRenderer.end()
    }
}