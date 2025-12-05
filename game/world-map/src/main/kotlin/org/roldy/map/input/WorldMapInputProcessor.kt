package org.roldy.map.input

import org.roldy.keybind.KeybindProcessor
import org.roldy.keybind.KeybindSettings

class WorldMapInputProcessor(override val settings: KeybindSettings) : KeybindProcessor {
    val zoom = ZoomCameraProcessor(settings)
    private val processors = listOf(zoom)

    override fun keyDown(keycode: Int): Boolean =
        processors.any { it.keyDown(keycode) }

    override fun keyUp(keycode: Int): Boolean =
        processors.any { it.keyUp(keycode) }

    override fun scrolled(amountX: Float, amountY: Float): Boolean =
        processors.any { it.scrolled(amountX, amountY) }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean =
        processors.any { it.mouseMoved(screenX, screenY) }
}