package org.roldy.core

import org.roldy.core.stream.Streamable

interface Renderable: Streamable {
    context(deltaTime: Float)
    fun render()
    fun dispose() {}
}