package org.roldy.core.stream.chunk

data class ChunkCoord(val cx: Int, val cy: Int)

class Chunk(val coord: ChunkCoord) {
    val items: MutableList<ChunkItemData> = mutableListOf()
    var loaded = false
}

