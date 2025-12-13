package org.roldy.gameplay.world.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter

class DebugInputProcessor(
    val onResetPosition: () -> Unit,
): InputAdapter() {


    override fun keyDown(keycode: Int): Boolean {
        if(keycode == Input.Keys.R) {
            onResetPosition()
            return true
        }
        return false
    }
}