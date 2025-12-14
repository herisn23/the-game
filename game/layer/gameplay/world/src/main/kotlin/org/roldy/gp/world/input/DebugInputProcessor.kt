package org.roldy.gp.world.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import org.roldy.core.TimeManager
import org.roldy.core.logger

class DebugInputProcessor(
    val timeManager: TimeManager,
    val onResetPosition: () -> Unit,
): InputAdapter() {


    override fun keyDown(keycode: Int): Boolean {
        if(keycode == Input.Keys.R) {
            onResetPosition()
            return true
        }
        if(keycode == Input.Keys.NUM_1) {
            logger.debug { "Game paused" }
            timeManager.pause()
            return true
        }
        if(keycode == Input.Keys.NUM_2) {
            logger.debug { "Game at speed 1" }
            timeManager.slowMotion()
            return true
        }
        if(keycode == Input.Keys.NUM_3) {
            logger.debug { "Game at speed 2" }
            timeManager.normalSpeed()
            return true
        }
        if(keycode == Input.Keys.NUM_4) {
            logger.debug { "Game at speed 3" }
            timeManager.fastForward()
            return true
        }
        return false
    }
}