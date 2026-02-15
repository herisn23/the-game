package org.roldy.core.collision

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox

class CollisionManager(
    val collisionSystem: CollisionSystem
) {
    private val tmpX: Vector3 = Vector3()
    private val tmpZ: Vector3 = Vector3()
    private val tmpBoxX = BoundingBox()
    private val tmpBoxZ = BoundingBox()

    /**
     * Checks for collisions along the X and Z axes independently and applies the movement
     * with collision-corrected values.
     *
     * This function performs collision detection by testing movement along the X axis and Z axis
     * separately. It temporarily positions a bounding box at the proposed locations and checks
     * if they intersect with any nearby colliders. For each axis, if a collision is detected,
     * that movement component is zeroed out. The apply callback is then invoked with the
     * collision-corrected movement values.
     *
     * @param tempBoundingBox The bounding box of the entity being checked for collisions
     * @param position The current position of the entity in world space
     * @param x The proposed movement distance along the X axis
     * @param z The proposed movement distance along the Z axis
     * @param apply Callback invoked with the collision-corrected x and z values (0f if collision detected)
     */
    fun check(
        tempBoundingBox: BoundingBox,
        position: Vector3,
        x: Float,
        z: Float,
        apply: (x: Float, z: Float) -> Unit
    ) {
        tmpX.set(position).add(collisionSystem.offsetProvider.shiftOffset)
        tmpZ.set(position).add(collisionSystem.offsetProvider.shiftOffset)

        tmpX.x += x
        tmpZ.z += z

        centerBoundingBox(tmpBoxX, tempBoundingBox, tmpX)
        centerBoundingBox(tmpBoxZ, tempBoundingBox, tmpZ)

        var intersectX = false
        var intersectZ = false
        val candidates = collisionSystem.findNearbyColliders(position)
        for (collider in candidates) {
            if (!intersectX)
                intersectX = collider.intersectsCollisionBounds(tmpBoxX)

            if (!intersectZ)
                intersectZ = collider.intersectsCollisionBounds(tmpBoxZ)

            if (intersectX && intersectZ) return // skip remaining checks if both axes already intersect
        }
        val nextX = x.takeIf { !intersectX } ?: 0f
        val nextZ = z.takeIf { !intersectZ } ?: 0f
        apply(nextX, nextZ)
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
}