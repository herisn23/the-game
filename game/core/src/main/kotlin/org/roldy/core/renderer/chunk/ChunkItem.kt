package org.roldy.core.renderer.chunk

interface ChunkItem<D:ChunkObjectData> {
    fun bind(data: D)
}