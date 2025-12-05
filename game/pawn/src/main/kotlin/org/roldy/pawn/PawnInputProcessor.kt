package org.roldy.pawn

import com.badlogic.gdx.Input
import org.roldy.core.keybind.KeybindName
import org.roldy.keybind.KeybindProcessor
import org.roldy.keybind.KeybindSettings
import org.roldy.pawn.skeleton.attribute.Back
import org.roldy.pawn.skeleton.attribute.Front
import org.roldy.pawn.skeleton.attribute.Left
import org.roldy.pawn.skeleton.attribute.Right

class PawnInputProcessor(override val settings: KeybindSettings, val currentPawn: () -> Pawn) : KeybindProcessor {

    var lastMove: KeybindName? = null
    var run = false
    var moving = false
    val speed
        get() = when {
            run -> currentPawn().runSpeed
            else -> currentPawn().walkSpeed
        }
    val keyActions = mapOf(
        KeybindName.MoveUp to (
                {
                    currentPawn().manager.currentOrientation = Back
                    currentPawn().manager.walk(speed)
                } to {
                    currentPawn().manager.stop()
                }),
        KeybindName.MoveLeft to (
                {
                    currentPawn().manager.currentOrientation = Left
                    currentPawn().manager.walk(speed)
                } to {
                    currentPawn().manager.stop()
                }),
        KeybindName.MoveDown to (
                {
                    currentPawn().manager.currentOrientation = Front
                    currentPawn().manager.walk(speed)
                } to {
                    currentPawn().manager.stop()
                }),
        KeybindName.MoveRight to (
                {
                    currentPawn().manager.currentOrientation = Right
                    currentPawn().manager.walk(speed)
                } to {
                    currentPawn().manager.stop()
                }
                )
    )
    val keyBinds = keyActions.keys

    private fun findKeyBind(keycode: Int): KeybindName? =
        keyBinds.find {
            settings[it] == keycode
        }

    override fun keyDown(keycode: Int): Boolean {
        val keybindName = findKeyBind(keycode)
        if (keybindName != null) {
            lastMove = keybindName
        }
        if (keycode == Input.Keys.SHIFT_LEFT) {
            run = true
        }
        return move()
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Input.Keys.SHIFT_LEFT) {
            run = false
            if (moving)
                return move()
        }
        val keybindName = findKeyBind(keycode)
        if (keybindName != lastMove) return false
        return stop()
    }

    private fun move() = run {
        moving = true
        keyActions[lastMove]?.first()?.let { true } ?: false
    }


    private fun stop() = run {
        moving = false
        (keyActions[lastMove]?.second()?.let { true } ?: false).also { lastMove = null }
    }
}