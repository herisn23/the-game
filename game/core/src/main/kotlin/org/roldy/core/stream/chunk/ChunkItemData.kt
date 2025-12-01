package org.roldy.core.stream.chunk
data class ChunkItemData(
    val type: String,
    val x: Float,
    val y: Float,
    val props: Map<String, Any> = emptyMap()
)