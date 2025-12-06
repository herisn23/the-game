package org.roldy.core.renderer.chunk

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2

data class ChunkObjectData(
    val atlas: TextureAtlas,
    val name: String,
    val isRoad: Boolean,
    val position: Vector2,
    val props: Map<String, Any> = emptyMap()
)