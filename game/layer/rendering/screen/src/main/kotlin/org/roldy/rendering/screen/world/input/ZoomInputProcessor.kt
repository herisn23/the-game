package org.roldy.rendering.screen.world.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import org.roldy.core.keybind.KeybindName
import org.roldy.core.keybind.KeybindSettings

class ZoomInputProcessor(
    val settings: KeybindSettings,
    val camera: OrthographicCamera,
    val minZoom: Float,
    val maxZoom: Float,
) : InputAdapter() {
    private val keyZoomSensitivity: Float = 15f
    private var zoomDirection: Float = 0f
    private var scrollZooming = false

    override fun keyDown(keycode: Int): Boolean {
        if (scrollZooming) {
            return false
        }
        if (keycode == settings[KeybindName.CameraZoomOut]) {
            zoomDirection = keyZoomSensitivity
            return true
        }
        if (keycode == settings[KeybindName.CameraZoomIn]) {
            zoomDirection = -keyZoomSensitivity
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if (scrollZooming) {
            return false
        }
        if (keycode == Input.Keys.Q) {
            zoomDirection = 0f
            return true
        }
        if (keycode == Input.Keys.E) {
            zoomDirection = 0f
            return true
        }
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        zoomDirection = amountY
        scrollZooming = true
        return true
    }

    /**
     * Call each frame
     */
    context(delta: Float)
    fun update() {
        camera.zoom += zoomDirection * delta
        camera.zoom = MathUtils.clamp(camera.zoom, minZoom, maxZoom)
        // When handling scroll zoom, reset the zoom after processing to stop continuous zooming.
        // Otherwise, zoomDirection will remain set to the last scroll amountY, causing infinite zoom.
        // Must be called each frame
        if (scrollZooming) {
            zoomDirection = 0f
            scrollZooming = false
        }
    }
}