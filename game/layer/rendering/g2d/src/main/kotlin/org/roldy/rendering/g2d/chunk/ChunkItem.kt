package org.roldy.rendering.g2d.chunk

interface ChunkItem<D:ChunkObjectData> {
    fun bind(data: D)
}