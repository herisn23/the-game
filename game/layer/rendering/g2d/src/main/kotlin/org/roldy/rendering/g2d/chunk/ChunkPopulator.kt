package org.roldy.rendering.g2d.chunk

import org.roldy.rendering.g2d.Layered

interface ChunkPopulator<D: ChunkObjectData, T : Chunk<D>> {
    fun populate(chunk: T): List<D>

    fun createPersistentItems(): List<Layered>
}