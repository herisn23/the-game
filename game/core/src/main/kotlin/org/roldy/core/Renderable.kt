package org.roldy.core

import org.roldy.core.renderer.Sortable

interface Renderable: Sortable {
    context(deltaTime: Float)
    fun render()
    fun dispose() {}
}