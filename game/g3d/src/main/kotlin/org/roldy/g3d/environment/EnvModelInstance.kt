package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import org.roldy.core.collision.MeshCollider

class EnvModelInstance(
    val name: String,
    collision: Model?,
    val foliage: Boolean,
    lod: Map<Int, ModelInstance>
) : MeshCollider {
    private val lod = lod.map { (key, instance) ->
        key to instance.wrapper(this)
    }.toMap()

    class ModelInstanceWrapper(
        val model: EnvModelInstance,
        val instance: ModelInstance,
        val boundingBox: BoundingBox
    )

    inner class Transform {
        internal val tmpPos = Vector3()
        internal val tmpRot = Quaternion()

        fun setTranslation(ox: Float, oy: Float, oz: Float) {
            tmpPos.set(ox, oy, oz)
        }

        fun setRotation(rotX: Float, rotY: Float, rotZ: Float) {
            tmpRot.set(Vector3.Y, rotY)
            tmpRot.set(Vector3.X, rotX)
            tmpRot.set(Vector3.Z, rotZ)
        }

        fun apply() {
            lod.forEach { (_, wrapper) ->
                wrapper.instance.transform.idt()
                wrapper.instance.transform.rotate(tmpRot)
                wrapper.instance.transform.setTranslation(tmpPos)
            }
            collisionInstance?.let { ci ->
                ci.transform.idt()
                ci.transform.setTranslation(tmpPos)
                ci.transform.rotate(tmpRot)
                // Calculate in local space first
                ci.calculateBoundingBox(collisionBoundingBox)
                // Then transform to world space
                collisionBoundingBox.mul(ci.transform)
            }
        }
    }

    val transform = Transform()

    companion object {
        const val LOD0_THRESHOLD = 30//50f
        const val LOD1_THRESHOLD = 60//100f
        const val LOD2_THRESHOLD = 90//200f
        const val LOD3_THRESHOLD = 120//300f
    }

    private val lodLevels = lod.keys.toList()
    private val maxLod = lodLevels.max()

    private val hasLod = lod.size > 1

    override val position get() = transform.tmpPos

    override val collisionBoundingBox = BoundingBox()
    val collisionInstance: ModelInstance? = collision?.let { ModelInstance(it) }

    private val hasCollision get() = collisionInstance != null

    /**
     * Check if a point is inside the collision bounds
     * @param point The point to check
     * @return true if point is inside collision bounds
     */
    @Suppress("unused")
    override fun isPointInCollision(point: Vector3): Boolean {
        return hasCollision && collisionBoundingBox.contains(point)
    }

    /**
     * Check if this model's bounding box intersects with another bounding box
     * @param other The other bounding box to check intersection with
     * @return true if bounding boxes intersect
     */
    @Suppress("unused")
    override fun intersectsCollisionBounds(other: BoundingBox): Boolean {
        return hasCollision && collisionBoundingBox.intersects(other)
    }

    /**
     * Raycast against this model's collision geometry
     * @param ray The ray to cast
     * @return true if a ray intersects collision bounds
     */
    @Suppress("unused")
    override fun rayCastCollision(ray: Ray): Boolean {
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

    private fun ModelInstance.wrapper(envModelInstance: EnvModelInstance) =
        ModelInstanceWrapper(
            envModelInstance,
            this,
            this.calculateBoundingBox(BoundingBox())
        )

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
    private fun getLodInstance(): ModelInstanceWrapper {
        val dist = camera.position.dst(position)
        val lodLevel = getLodLevel(dist)
        return lod.getValue(lodLevel)
    }

    context(camera: Camera)
    fun get(): ModelInstanceWrapper =
        if (hasLod) {
            getLodInstance()
        } else {
            lod.getValue(-1)
        }


}

