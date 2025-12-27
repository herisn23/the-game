package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Action

fun action(act: (Float) -> Unit) = object : Action() {
    override fun act(delta: Float): Boolean {
        act(delta)
        return false
    }

}