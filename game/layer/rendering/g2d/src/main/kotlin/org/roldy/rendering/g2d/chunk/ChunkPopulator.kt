package org.roldy.rendering.g2d.chunk

fun interface ChunkPopulator<D: ChunkObjectData, T : Chunk<D>> {
    fun populate(chunk: T): List<D>
}