package org.roldy.core.stream

data class MapItemData(
    val type: String,
    val x: Float,
    val y: Float,
    val props: Map<String, Any> = emptyMap()
)

data class ChunkCoord(val cx: Int, val cy: Int)

class Chunk(val coord: ChunkCoord) {
    val items: MutableList<MapItemData> = mutableListOf()
    var loaded = false
}