package org.roldy.core

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor

class InputProcessorDelegate(
    val delegates: List<InputProcessor>
) : InputAdapter() {

    override fun keyDown(keycode: Int): Boolean =
        delegates.any { it.keyDown(keycode) }

    override fun keyUp(keycode: Int): Boolean =
        delegates.any { it.keyUp(keycode) }

    override fun keyTyped(character: Char): Boolean =
        delegates.any { it.keyTyped(character) }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean =
        delegates.any { it.mouseMoved(screenX, screenY) }

    override fun scrolled(amountX: Float, amountY: Float): Boolean =
        delegates.any { it.scrolled(amountX, amountY) }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
        delegates.any { it.touchDown(screenX, screenY, pointer, button) }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
        delegates.any { it.touchUp(screenX, screenY, pointer, button) }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean =
        delegates.any { it.touchDragged(screenX, screenY, pointer) }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
        delegates.any { it.touchCancelled(screenX, screenY, pointer, button) }
}