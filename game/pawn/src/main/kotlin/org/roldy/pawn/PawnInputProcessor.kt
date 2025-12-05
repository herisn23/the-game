package org.roldy.pawn

import org.roldy.core.keybind.KeybindName
import org.roldy.keybind.KeybindProcessor
import org.roldy.keybind.KeybindSettings
import org.roldy.pawn.skeleton.attribute.Back
import org.roldy.pawn.skeleton.attribute.Front
import org.roldy.pawn.skeleton.attribute.Left
import org.roldy.pawn.skeleton.attribute.Right

class PawnInputProcessor(override val settings: KeybindSettings,val currentPawn: () -> Pawn) : KeybindProcessor {

    var lastKeycode: Int = 0
    val keyActions = mapOf(
        KeybindName.MoveUp to (
                {
                    currentPawn().pawnManager.currentOrientation = Back
                    currentPawn().pawnManager.walk(currentPawn().speed)
                } to {
                    currentPawn().pawnManager.stop()
                }),
        KeybindName.MoveLeft to (
                {
                    currentPawn().pawnManager.currentOrientation = Left
                    currentPawn().pawnManager.walk(currentPawn().speed)
                } to {
                    currentPawn().pawnManager.stop()
                }),
        KeybindName.MoveDown to (
                {
                    currentPawn().pawnManager.currentOrientation = Front
                    currentPawn().pawnManager.walk(currentPawn().speed)
                } to {
                    currentPawn().pawnManager.stop()
                }),
        KeybindName.MoveRight to (
                {
                    currentPawn().pawnManager.currentOrientation = Right
                    currentPawn().pawnManager.walk(currentPawn().speed)
                } to {
                    currentPawn().pawnManager.stop()
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
            lastKeycode = keycode
        }
        return keyActions[keybindName]?.first()?.let { true } ?: false
    }

    override fun keyUp(keycode: Int): Boolean {
        val keybindName = findKeyBind(keycode)
        if (keycode != lastKeycode) return false
        return keyActions[keybindName]?.second()?.let { true } ?: false
    }
}