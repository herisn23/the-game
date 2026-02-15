package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.collision.CollisionUtils
import org.roldy.core.collision.MeshCollider
import org.roldy.core.collision.MeshCollisionData
import org.roldy.core.collision.MeshCollisionData.Triangle

class EnvModelInstance(
    val name: String,
    collisionModel: Model?,
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

    private var collisionData: List<Triangle> = emptyList()

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
                collisionData = MeshCollisionData.extractTriangles(ci)
            }
        }
    }

    val transform = Transform()
    val shiftedPosition = Vector3()

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
    val collisionInstance: ModelInstance? = collisionModel?.let { ModelInstance(it) }

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
    override fun intersectsCollisionBounds(other: BoundingBox): Boolean {
        if (!hasCollision) return false
        // Broad phase: AABB check first (cheap)
        if (!collisionBoundingBox.intersects(other)) return false
        if (collisionData.isEmpty()) return false
        // Narrow phase: triangle check (accurate)
        return collisionData.any { tri ->
            CollisionUtils.aabbIntersectsTriangle(other.min, other.max, tri)
        }
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
    private fun getLodInstance(offsetProvider: OffsetProvider): ModelInstanceWrapper {
        val pos = shiftedPosition.set(position).sub(offsetProvider.shiftOffset)
        val dist = camera.position.dst(pos)
        val lodLevel = getLodLevel(dist)
        return lod.getValue(lodLevel)
    }

    context(camera: Camera)
    fun get(offsetProvider: OffsetProvider): ModelInstanceWrapper =
        if (hasLod) {
            getLodInstance(offsetProvider)
        } else {
            lod.getValue(-1)
        }
}

