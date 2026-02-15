package org.roldy.core.collision

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.camera.OffsetProvider

data class CollisionResult(
    val collided: Boolean,
    val penetration: Vector3 = Vector3.Zero, // how far inside
    val normal: Vector3 = Vector3.Zero,       // surface normal to push out along
    val collider: MeshCollider? = null
)

class CollisionSystem(
    val offsetProvider: OffsetProvider,
    val colliders: () -> List<MeshCollider>
) {
    private val radius = 5f
    private val tmpBox = BoundingBox()

    /**
     * Check collision and return the deepest penetrating result.
     * For multiple collisions, you could return a list instead.
     */
    fun checkCollision(
        position: Vector3,
        boundingBox: BoundingBox
    ): CollisionResult {
        // Reposition bounding box around the current position
        centerBoundingBox(tmpBox, boundingBox, position)

        val candidates = findNearbyColliders(position)

        var deepest = CollisionResult(false)
        var maxPenDepth = 0f

        for (collider in candidates) {
            if (!collider.intersectsCollisionBounds(tmpBox)) continue

            // Calculate penetration using AABB overlap
            val pen = calcPenetration(tmpBox, collider.collisionBoundingBox)
            val depth = pen.len2()

            if (depth > maxPenDepth) {
                maxPenDepth = depth
                deepest = CollisionResult(
                    collided = true,
                    penetration = Vector3(pen),
                    normal = Vector3(pen).nor(),
                    collider = collider
                )
            }
        }

        return deepest
    }

    /**
     * Minimum Translation Vector — shortest push to separate two AABBs.
     */
    private fun calcPenetration(a: BoundingBox, b: BoundingBox): Vector3 {
        val overlapX = minOf(a.max.x - b.min.x, b.max.x - a.min.x)
        val overlapY = minOf(a.max.y - b.min.y, b.max.y - a.min.y)
        val overlapZ = minOf(a.max.z - b.min.z, b.max.z - a.min.z)

        // Push out along the axis with smallest overlap (MTV)
        return when {
            overlapX <= overlapY && overlapX <= overlapZ -> {
                val sign = if (a.centerX < b.centerX) -1f else 1f
                Vector3(overlapX * sign, 0f, 0f)
            }

            overlapY <= overlapX && overlapY <= overlapZ -> {
                val sign = if (a.centerY < b.centerY) -1f else 1f
                Vector3(0f, overlapY * sign, 0f)
            }

            else -> {
                val sign = if (a.centerZ < b.centerZ) -1f else 1f
                Vector3(0f, 0f, overlapZ * sign)
            }
        }
    }

    private fun centerBoundingBox(out: BoundingBox, box: BoundingBox, pos: Vector3) {
        val halfW = (box.max.x - box.min.x) / 2f
        val halfH = (box.max.y - box.min.y) / 2f
        val halfD = (box.max.z - box.min.z) / 2f
        out.min.set(pos.x - halfW, pos.y - halfH, pos.z - halfD)
        out.max.set(pos.x + halfW, pos.y + halfH, pos.z + halfD)
    }

    fun findNearbyColliders(
        target: Vector3
    ): List<MeshCollider> {
        val radiusSq = radius * radius // avoid sqrt
        val pos = Vector3()
        return colliders().filter {
            pos.set(it.position).sub(offsetProvider.shiftOffset).dst2(target) <= radiusSq
        }
    }
}