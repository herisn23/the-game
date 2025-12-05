package org.roldy

import com.badlogic.gdx.InputAdapter
import org.roldy.keybind.KeybindProcessor

class InputProcessorDelegate(
    val delegates: List<KeybindProcessor>
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
}