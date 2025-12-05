package org.roldy.core.renderer.chunk

fun interface ChunkPopulator<T : Chunk> {
    fun populate(chunk: T): List<ChunkObjectData>
}