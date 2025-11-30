package org.roldy.core.renderer

import org.roldy.core.RenderedObject

interface LayeredObject: RenderedObject {
    val pivotX: Float
    val pivotY: Float
}