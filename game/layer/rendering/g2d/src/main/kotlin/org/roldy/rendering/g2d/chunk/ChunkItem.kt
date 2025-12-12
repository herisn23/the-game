package org.roldy.rendering.g2d.chunk

import org.roldy.rendering.g2d.Layered

interface ChunkItem<D:ChunkObjectData>: Layered {
    var data: D?
}