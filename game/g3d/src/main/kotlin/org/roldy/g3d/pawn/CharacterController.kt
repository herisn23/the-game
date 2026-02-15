package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.camera.SimpleThirdPersonCamera
import org.roldy.core.collision.CollisionSystem
import org.roldy.core.logger
import org.roldy.g3d.terrain.TerrainSampler

class CharacterController(
    val entity: ModelInstance,
    private val heightSampler: TerrainSampler,
    private val camera: SimpleThirdPersonCamera,
    private val collisionSystem: CollisionSystem
) {
    private val logger by logger()
    private val currentPosition = Vector3()
    private val tempBoundingBox = BoundingBox()
    private val tmpBox = BoundingBox()
    private val previousPosition = Vector3()
    private val maxResolveIterations = 3 // prevent infinite loops

    var moveSpeed = 2f
    var heightOffset = 0f
    var checkCollision = false
    private var initialized = false

    init {
        entity.calculateBoundingBox(tempBoundingBox)
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
        val dy = currentPosition.y - previousPosition.y
        val dz = currentPosition.z - previousPosition.z

        // Reset to previous, then apply axis by axis
        currentPosition.set(previousPosition)

        centerBoundingBox(tmpBox, tempBoundingBox, currentPosition)

        currentPosition.x += dx
        if (checkCollision && checkCollisionAt(currentPosition)) {
            currentPosition.x = previousPosition.x // rollback X
        }

        // Try Z
        currentPosition.z += dz
        if (checkCollision && checkCollisionAt(currentPosition)) {
            currentPosition.z = previousPosition.z // rollback Z
        }

        // Apply terrain height (Y is driven by terrain, not physics)
        if (heightSampler.isInBounds(currentPosition.x, currentPosition.z)) {
            val terrainHeight = heightSampler.getHeightAt(currentPosition.x, currentPosition.z)
            currentPosition.y = terrainHeight + heightOffset
        }

        applyTransform()
    }

    private fun checkCollisionAt(position: Vector3): Boolean {
        centerBoundingBox(tmpBox, tempBoundingBox, position)

        val candidates = collisionSystem.findNearbyColliders(position)
        for (collider in candidates) {
            if (collider.intersectsCollisionBounds(tmpBox)) {
                return true
            }
        }
        return false
    }

    private fun centerBoundingBox(out: BoundingBox, source: BoundingBox, pos: Vector3) {
        val hw = source.width / 2f
        val hh = source.height / 2f
        val hd = source.depth / 2f
        out.set(
            Vector3(pos.x - hw, pos.y - hh, pos.z - hd),
            Vector3(pos.x + hw, pos.y + hh, pos.z + hd)
        )
    }

    private fun resolveCollisions() {
        for (i in 0 until maxResolveIterations) {
            val result = collisionSystem.checkCollision(currentPosition, tempBoundingBox)

            if (!result.collided) return // clean — no collision

            // Push the player out by the penetration vector
            currentPosition.add(result.penetration)

            // Optional: cancel velocity along collision normal so you
            // don't keep pushing into the wall next frame
            // velocity.sub(tmpVec.set(result.normal).scl(velocity.dot(result.normal)))
        }

        // If still stuck after iterations, snap back (safety net)
        // currentPosition.set(previousPosition)
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

        // Immediately update the camera to a new position - this prevents fade/jump
        camera.updateCameraPosition(currentPosition)

        // Update entity transform
        applyTransform()

        println("Character shifted: pos=$currentPosition")
    }
}