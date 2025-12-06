package org.roldy.core.renderer.chunk

fun interface ChunkPopulator<D: ChunkObjectData, T : Chunk<D>> {
    fun populate(chunk: T): List<D>
}