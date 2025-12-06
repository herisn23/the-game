package org.roldy.environment

import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.core.renderer.chunk.ChunkObjectData

data class MapObjectData(
    override val name: String,
    override val position: Vector2,
    val coords: Vector2Int,
    val createBehaviour: (MapObjectData) -> MapBehaviourObject
) : ChunkObjectData