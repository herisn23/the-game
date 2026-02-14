package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray

class EnvModelInstance(
    val name: String,
    collision: Model?,
    val foliage: Boolean,
    val lod: Map<Int, ModelInstance>
) {

    companion object {
        const val LOD0_THRESHOLD = 50f
        const val LOD1_THRESHOLD = 100f
        const val LOD2_THRESHOLD = 200f
        const val LOD3_THRESHOLD = 300f
    }

    private val lodLevels = lod.keys.toList()
    private val maxLod = lodLevels.max()
    private val tmpPos = Vector3()
    private val hasLod = lod.size > 1
    private val firstInstance = lod.values.first()
    private var lastInstance = firstInstance
    private val position get() = firstInstance.transform.getTranslation(tmpPos)

    private val collisionBoundingBox = BoundingBox()
    private val collisionInstance: ModelInstance? = collision?.let { ModelInstance(it) }

    private val hasCollision get() = collisionInstance != null

    fun setRotation(rotX: Float, rotY: Float, rotZ: Float) {
        lod.forEach { (_, instance) ->
            instance.transform.rotate(Vector3.Y, rotY)
            instance.transform.rotate(Vector3.X, rotX)
            instance.transform.rotate(Vector3.Z, rotZ)
        }
    }
    fun setTranslation(ox: Float, oy: Float, oz: Float) {
        lod.forEach { (_, instance) ->
            instance.transform.idt()
            instance.transform.setTranslation(ox, oy, oz)
        }
        // Update collision instance position if it exists
        collisionInstance?.let { ci ->
            ci.transform.idt()
            ci.transform.setTranslation(ox, oy, oz)
            ci.calculateBoundingBox(collisionBoundingBox)
        }
    }

    /**
     * Get the bounding box for this model's collision geometry
     * @return BoundingBox if collision geometry exists, null otherwise
     */
    @Suppress("unused")
    fun getCollisionBounds(): BoundingBox? {
        return if (hasCollision) collisionBoundingBox else null
    }

    /**
     * Check if a point is inside the collision bounds
     * @param point The point to check
     * @return true if point is inside collision bounds
     */
    @Suppress("unused")
    fun isPointInCollision(point: Vector3): Boolean {
        return hasCollision && collisionBoundingBox.contains(point)
    }

    /**
     * Check if this model's bounding box intersects with another bounding box
     * @param other The other bounding box to check intersection with
     * @return true if bounding boxes intersect
     */
    @Suppress("unused")
    fun intersectsCollisionBounds(other: BoundingBox): Boolean {
        return hasCollision && collisionBoundingBox.intersects(other)
    }

    /**
     * Raycast against this model's collision geometry
     * @param ray The ray to cast
     * @return true if ray intersects collision bounds
     */
    @Suppress("unused")
    fun raycastCollision(ray: Ray): Boolean {
        if (!hasCollision) return false

        // Check if ray origin is inside bounds
        if (collisionBoundingBox.contains(ray.origin)) return true

        // Simple ray-AABB intersection test
        val tmpVec = Vector3()
        val stepSize = 1f
        var distance = 0f
        val maxDistance = 1000f

        while (distance < maxDistance) {
            tmpVec.set(ray.direction).scl(distance).add(ray.origin)
            if (collisionBoundingBox.contains(tmpVec)) return true
            distance += stepSize
        }

        return false
    }

    /**
     * Get the collision instance for advanced collision operations
     * @return ModelInstance representing collision geometry, or null if no collision model
     */
    @Suppress("unused")
    fun getCollisionInstance(): ModelInstance? = collisionInstance

    private fun getLodLevel(dist: Float) =
        when {
            dist < LOD0_THRESHOLD -> 0
            dist < LOD1_THRESHOLD -> 1
            dist < LOD2_THRESHOLD -> 2
            dist < LOD3_THRESHOLD -> 3
            else -> -1
        }.clampLodLevel()

    private fun Int.clampLodLevel(): Int {
        if (this < 0) return maxLod
        if (this >= lodLevels.size) return maxLod
        return this
    }

    context(camera: Camera)
    private fun getLodInstance(): ModelInstance {
        val dist = camera.position.dst(position)
        val lodLevel = getLodLevel(dist)
        return lod.getValue(lodLevel)
    }

    context(camera: Camera)
    fun instance(): ModelInstance =
        if (hasLod) {
            getLodInstance()
        } else {
            lod.getValue(-1)
        }


}

