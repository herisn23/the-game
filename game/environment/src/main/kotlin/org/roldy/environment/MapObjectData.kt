package org.roldy.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import org.roldy.core.renderer.chunk.ChunkObjectData

data class MapObjectData(
    val atlas: TextureAtlas,
    val isRoad: Boolean,
    val props: Map<String, Any> = emptyMap(),
    override val name: String,
    override val position: Vector2
): ChunkObjectData