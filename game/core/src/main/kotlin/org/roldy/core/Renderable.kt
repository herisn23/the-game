package org.roldy.core

import org.roldy.core.renderer.Layered

interface Renderable: Layered {
    context(deltaTime: Float)
    fun render()
    fun dispose() {}
}