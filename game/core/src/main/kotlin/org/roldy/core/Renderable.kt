package org.roldy.core

import com.badlogic.gdx.graphics.g3d.Environment

interface Renderable {

    context(delta: Float, env: Environment)
    fun render()
}