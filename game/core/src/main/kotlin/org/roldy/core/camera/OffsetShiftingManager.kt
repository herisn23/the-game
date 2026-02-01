package org.roldy.core.camera

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3

open class OffsetShiftingManager : OffsetProvider {

    private val totalOffset = Vector3()  // Accumulated world offset
    private val position = Vector3()
    private val threshold = 1000f // Shift when player exceeds this distance
    private val thresholdRange = -threshold..threshold

    // Changed: provides shift amount, not total offset
    var onShift: (shiftX: Float, shiftZ: Float, totalOffset: Vector3) -> Unit = { _, _, _ -> }
    override val shiftOffset: Vector3
        get() = totalOffset
    private val Vector3.needsShift
        get() =
            x !in thresholdRange || z !in thresholdRange

    fun update(player: ModelInstance) {
        val playerPosition = player.transform.getTranslation(position)
        if (playerPosition.needsShift) {

            // Store shift amount (XZ only, never shift Y)
            val shiftX = playerPosition.x
            val shiftZ = playerPosition.z

            // Reset player XZ, keep Y unchanged
            playerPosition.x = 0f
            playerPosition.z = 0f
            // playerPosition.y stays the same!
            player.transform.setTranslation(playerPosition)

            // ACCUMULATE offset (don't overwrite!)
            totalOffset.x += shiftX
            totalOffset.z += shiftZ

            onShift(shiftX, shiftZ, totalOffset)
        }
    }

    // Convert local position back to world coordinates
    fun toWorldPosition(localPos: Vector3): Vector3 {
        return Vector3(localPos).add(totalOffset)
    }

    // Convert world position to local coordinates
    fun toLocalPosition(worldPos: Vector3): Vector3 {
        return Vector3(worldPos).sub(totalOffset)
    }

    val renderOffset get() = totalOffset
}