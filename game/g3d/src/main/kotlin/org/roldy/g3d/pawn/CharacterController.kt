package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.camera.SimpleThirdPersonCamera
import org.roldy.core.collision.CollisionManager
import org.roldy.core.collision.CollisionSystem
import org.roldy.core.logger
import org.roldy.g3d.terrain.TerrainSampler

class CharacterController(
    val entity: ModelInstance,
    private val heightSampler: TerrainSampler,
    private val camera: SimpleThirdPersonCamera,
    collisionSystem: CollisionSystem
) {
    private val collisionManager = CollisionManager(collisionSystem)
    private val logger by logger()
    private val currentPosition = Vector3()
    private val tempBoundingBox = BoundingBox()

    private val previousPosition = Vector3()

    var moveSpeed = 2f
    var heightOffset = 0f
    var checkCollision = true
    private var initialized = false
    val position: Vector3 get() = currentPosition

    init {
        entity.calculateBoundingBox(tempBoundingBox)
        val characterWidth = 0.6f
        val characterHeight = 1.8f
        val characterDepth = 0.6f
        tempBoundingBox.set(
            Vector3(-characterWidth / 2f, 0f, -characterDepth / 2f),
            Vector3(characterWidth / 2f, characterHeight, characterDepth / 2f)
        )
    }

    fun initializeAt(x: Float, z: Float) {
        currentPosition.set(x, 0f, z)
        if (heightSampler.isInBounds(x, z)) {
            currentPosition.y = heightSampler.getHeightAt(x, z) + heightOffset
            logger.info("Initialize at: $currentPosition")
        }
        previousPosition.set(currentPosition)
        applyTransform()
        initialized = true
    }

    context(delta: Float)
    fun update() {
        if (!initialized) return
        // Save position before movement
        previousPosition.set(currentPosition)
        // Camera handles movement input and updates position
        camera.update(currentPosition, delta, moveSpeed)


        // Calculate the delta the camera wanted to apply
        val dx = currentPosition.x - previousPosition.x
        val dz = currentPosition.z - previousPosition.z

        // Reset to previous, then apply axis by axis
        currentPosition.set(previousPosition)
        if (checkCollision) {
            collisionManager.check(
                tempBoundingBox,
                currentPosition,
                dx,
                dz
            ) { x, z ->
                currentPosition.x += x
                currentPosition.z += z
            }
        } else {
            currentPosition.x += dx
            currentPosition.z += dz
        }

        // Apply terrain height (Y is driven by terrain, not physics)
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

    /**
     * Call this when floating origin shifts.
     */
    fun onOriginShift(shiftX: Float, shiftZ: Float) {
        currentPosition.x -= shiftX
        currentPosition.z -= shiftZ

        // Immediately update the camera to a new position - this prevents fade/jump
        camera.updateCameraPosition(currentPosition)

        // Update entity transform
        applyTransform()

        println("Character shifted: pos=$currentPosition")
    }
}