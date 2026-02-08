package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import org.roldy.core.camera.SimpleThirdPersonCamera
import org.roldy.core.logger
import org.roldy.g3d.terrain.TerrainSampler

class CharacterController(
    val entity: ModelInstance,
    private val heightSampler: TerrainSampler,
    private val camera: SimpleThirdPersonCamera
) {
    private val logger by logger()
    private val currentPosition = Vector3()

    var moveSpeed = 400f
    var heightOffset = 1f

    private var initialized = false

    fun initializeAt(x: Float, z: Float) {
        currentPosition.set(x, 0f, z)
        if (heightSampler.isInBounds(x, z)) {
            currentPosition.y = heightSampler.getHeightAt(x, z) + heightOffset
            logger.info("Initialize at: $currentPosition")
        }
        applyTransform()
        initialized = true
    }

    context(delta: Float)
    fun update() {
        if (!initialized) return

        // Camera handles movement input and updates position
        camera.update(currentPosition, delta, moveSpeed)

        // Apply terrain height
        if (heightSampler.isInBounds(currentPosition.x, currentPosition.z)) {
            val terrainHeight = heightSampler.getHeightAt(currentPosition.x, currentPosition.z)
            currentPosition.y = terrainHeight + heightOffset
        }

        applyTransform()
    }

    private fun applyTransform() {
        entity.transform.idt()
        entity.transform.rotate(Vector3.Y, camera.characterRotation)
        entity.transform.setTranslation(currentPosition)
    }

    fun getPosition(): Vector3 = currentPosition

    /**
     * Call this when floating origin shifts.
     */
    fun onOriginShift(shiftX: Float, shiftZ: Float) {
        currentPosition.x -= shiftX
        currentPosition.z -= shiftZ

        // Immediately update camera to new position - this prevents fade/jump
        camera.updateCameraPosition(currentPosition)

        // Update entity transform
        applyTransform()

        println("Character shifted: pos=$currentPosition")
    }
}