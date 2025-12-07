package org.roldy.map.input

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor

class WorldMapInputProcessor(
    private val processors:List<InputProcessor>
) : InputAdapter() {

    override fun keyDown(keycode: Int): Boolean =
        processors.any { it.keyDown(keycode) }

    override fun keyUp(keycode: Int): Boolean =
        processors.any { it.keyUp(keycode) }

    override fun scrolled(amountX: Float, amountY: Float): Boolean =
        processors.any { it.scrolled(amountX, amountY) }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean =
        processors.any { it.mouseMoved(screenX, screenY) }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
        processors.any { it.touchDown(screenX, screenY, pointer, button) }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
        processors.any { it.touchUp(screenX, screenY, pointer, button) }
}