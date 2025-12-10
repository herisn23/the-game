package org.roldy.rendering.g2d

import com.badlogic.gdx.utils.Disposable

interface Renderable: Layered, Disposable {
    context(deltaTime: Float)
    fun render()
}