package org.roldy.core.camera

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3

interface FloatingOriginEntity {
    var position: Vector3
}

abstract class FloatingOriginModelInstance(model: Model) : ModelInstance(model), FloatingOriginEntity {
    private val vector: Vector3 = Vector3()
    override var position: Vector3
        get() = transform.getTranslation(vector)
        set(value) {
            transform.setTranslation(value)
        }
}

open class FloatingOriginManager {

    private val offset = Vector3()
    private val threshold = 1000f // Shift when player exceeds this distance

    fun update(player: FloatingOriginEntity, allEntities: List<FloatingOriginEntity>) {
        // Check if player is too far from origin
        val playerPosition = player.position
        if (playerPosition.len() > threshold) {
            // Calculate shift amount
            val shift = Vector3(playerPosition)

            // Move everything back toward origin
            playerPosition.sub(shift)
            player.position = playerPosition

            // Shift all other entities
            allEntities.forEach { entity ->
                val vec = entity.position
                vec.sub(shift)
                entity.position = vec
            }

            // Track total offset for world coordinates if needed
            offset.add(shift)
        }
    }

    // Convert local position back to world coordinates
    fun toWorldPosition(localPos: Vector3): Vector3 {
        return Vector3(localPos).add(offset)
    }

    // Convert world position to local coordinates
    fun toLocalPosition(worldPos: Vector3): Vector3 {
        return Vector3(worldPos).sub(offset)
    }
}