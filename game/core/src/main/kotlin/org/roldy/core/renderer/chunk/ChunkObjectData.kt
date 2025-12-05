package org.roldy.core.renderer.chunk
data class ChunkObjectData(
    val region: String?,
    val x: Float,
    val y: Float,
    val props: Map<String, Any> = emptyMap()
)