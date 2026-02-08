package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import org.roldy.g3d.terrain.TerrainSampler

class RTSCharacterController(
    val entity: ModelInstance,
    private val heightSampler: TerrainSampler
) {
    private val targetPosition = Vector3()
    private val direction = Vector3()
    private val currentPosition = Vector3()

    var moveSpeed = 1f
    var rotationSpeed = 10f
    var arrivalThreshold = 0.5f
    var heightOffset = 1f

    private var currentRotation = 0f
    private var isMoving = false
    private var initialized = false

    fun setTarget(target: Vector3) {
        targetPosition.set(target.x, 0f, target.z)
        isMoving = true
    }

    fun initializeAt(x: Float, z: Float) {
        currentPosition.set(x, 0f, z)
        if (heightSampler.isInBounds(x, z)) {
            currentPosition.y = heightSampler.getHeightAt(x, z) + heightOffset
        }
        applyTransform()
        initialized = true
    }

    fun update(delta: Float) {
        if (!initialized) {
            initializeAt(0f, 0f)
            return
        }

        // Read current position from entity (in case it was shifted externally)
        currentPosition.set(entity.transform.getTranslation(currentPosition))

        if (isMoving) {
            direction.set(
                targetPosition.x - currentPosition.x,
                0f,
                targetPosition.z - currentPosition.z
            )
            val dist = direction.len()

            if (dist <= arrivalThreshold) {
                isMoving = false
            } else {
                direction.nor()
                val move = minOf(moveSpeed * delta, dist)
                currentPosition.x += direction.x * move
                currentPosition.z += direction.z * move

                val targetRotation = MathUtils.atan2(direction.x, direction.z) * MathUtils.radiansToDegrees
                currentRotation = lerpAngle(currentRotation, targetRotation, rotationSpeed * delta)
            }
        }

        if (heightSampler.isInBounds(currentPosition.x, currentPosition.z)) {
            val terrainHeight = heightSampler.getHeightAt(currentPosition.x, currentPosition.z)
            currentPosition.y = terrainHeight + heightOffset
        }

        applyTransform()
    }

    private fun applyTransform() {
        val instance = entity
        instance.transform.idt()
        instance.transform.rotate(Vector3.Y, currentRotation)
        instance.transform.setTranslation(currentPosition)
    }

    private fun lerpAngle(from: Float, to: Float, t: Float): Float {
        var diff = to - from
        while (diff > 180f) diff -= 360f
        while (diff < -180f) diff += 360f
        return from + diff * t.coerceIn(0f, 1f)
    }

    /**
     * Call this when floating origin shifts.
     * Must shift both current position AND target position.
     */
    fun onOriginShift(shiftX: Float, shiftZ: Float) {
        currentPosition.x -= shiftX
        currentPosition.z -= shiftZ

        targetPosition.x -= shiftX
        targetPosition.z -= shiftZ

        println("Character shifted: currentPos=$currentPosition, targetPos=$targetPosition")
    }
}