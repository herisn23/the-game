package org.roldy.core

interface RenderedObject {
    context(deltaTime: Float)
    fun render()
    fun dispose() {}
}