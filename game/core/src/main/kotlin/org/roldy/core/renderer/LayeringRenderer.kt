package org.roldy.core.renderer

import org.roldy.core.RenderedObject

open class LayeringRenderer(
    val objects: List<LayeredObject>
) : RenderedObject {

    context(deltaTime: Float)
    override fun render() {
        objects
            .sortedBy { -it.pivotY }
            .forEach { renderer ->
                renderer.render()
            }
    }


}