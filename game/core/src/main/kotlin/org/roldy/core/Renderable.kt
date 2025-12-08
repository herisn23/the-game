package org.roldy.core

import com.badlogic.gdx.utils.Disposable
import org.roldy.core.renderer.Layered

interface Renderable: Layered, Disposable {
    context(deltaTime: Float)
    fun render()
}