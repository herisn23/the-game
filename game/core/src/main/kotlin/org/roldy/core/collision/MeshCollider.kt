package org.roldy.core.collision

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray

interface MeshCollider {
    val position: Vector3
    val collisionBoundingBox: BoundingBox
    fun intersectsCollisionBounds(other: BoundingBox): Boolean
    fun rayCastCollision(ray: Ray): Boolean
    fun isPointInCollision(point: Vector3): Boolean
}