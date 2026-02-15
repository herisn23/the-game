package org.roldy.core.collision

import com.badlogic.gdx.math.Vector3

object CollisionUtils {

    private val tmpEdge0 = Vector3()
    private val tmpEdge1 = Vector3()
    private val tmpEdge2 = Vector3()
    private val tmpNormal = Vector3()
    private val tmpVMin = Vector3()
    private val tmpVMax = Vector3()

    /**
     * SAT-based AABB vs Triangle test
     * Based on Tomas Akenine-Möller's algorithm
     */
    fun aabbIntersectsTriangle(
        boxMin: Vector3, boxMax: Vector3,
        tri: MeshCollisionData.Triangle
    ): Boolean {
        val center = Vector3(
            (boxMin.x + boxMax.x) * 0.5f,
            (boxMin.y + boxMax.y) * 0.5f,
            (boxMin.z + boxMax.z) * 0.5f
        )
        val halfSize = Vector3(
            (boxMax.x - boxMin.x) * 0.5f,
            (boxMax.y - boxMin.y) * 0.5f,
            (boxMax.z - boxMin.z) * 0.5f
        )

        // Translate triangle to box center
        val v0 = Vector3(tri.v0).sub(center)
        val v1 = Vector3(tri.v1).sub(center)
        val v2 = Vector3(tri.v2).sub(center)

        val e0 = tmpEdge0.set(v1).sub(v0)
        val e1 = tmpEdge1.set(v2).sub(v1)
        val e2 = tmpEdge2.set(v0).sub(v2)

        // 9 axis cross product tests
        if (!axisTestX(e0.z, e0.y, v0, v2, halfSize)) return false
        if (!axisTestX(e1.z, e1.y, v1, v0, halfSize)) return false
        if (!axisTestX(e2.z, e2.y, v2, v1, halfSize)) return false

        if (!axisTestY(e0.z, e0.x, v0, v2, halfSize)) return false
        if (!axisTestY(e1.z, e1.x, v1, v0, halfSize)) return false
        if (!axisTestY(e2.z, e2.x, v2, v1, halfSize)) return false

        if (!axisTestZ(e0.y, e0.x, v0, v2, halfSize)) return false
        if (!axisTestZ(e1.y, e1.x, v1, v0, halfSize)) return false
        if (!axisTestZ(e2.y, e2.x, v2, v1, halfSize)) return false

        // Test AABB axes
        val minX = minOf(v0.x, v1.x, v2.x)
        val maxX = maxOf(v0.x, v1.x, v2.x)
        if (minX > halfSize.x || maxX < -halfSize.x) return false

        val minY = minOf(v0.y, v1.y, v2.y)
        val maxY = maxOf(v0.y, v1.y, v2.y)
        if (minY > halfSize.y || maxY < -halfSize.y) return false

        val minZ = minOf(v0.z, v1.z, v2.z)
        val maxZ = maxOf(v0.z, v1.z, v2.z)
        if (minZ > halfSize.z || maxZ < -halfSize.z) return false

        // Test triangle normal
        tmpNormal.set(e0).crs(e1)
        val d = -tmpNormal.dot(v0)
        return planeBoxOverlap(tmpNormal, d, halfSize)
    }

    private fun axisTestX(a: Float, b: Float, v0: Vector3, v1: Vector3, h: Vector3): Boolean {
        val p0 = a * v0.y - b * v0.z
        val p1 = a * v1.y - b * v1.z
        val min = minOf(p0, p1)
        val max = maxOf(p0, p1)
        val rad = Math.abs(a) * h.y + Math.abs(b) * h.z
        return min <= rad && max >= -rad
    }

    private fun axisTestY(a: Float, b: Float, v0: Vector3, v1: Vector3, h: Vector3): Boolean {
        val p0 = -a * v0.x + b * v0.z
        val p1 = -a * v1.x + b * v1.z
        val min = minOf(p0, p1)
        val max = maxOf(p0, p1)
        val rad = Math.abs(a) * h.x + Math.abs(b) * h.z
        return min <= rad && max >= -rad
    }

    private fun axisTestZ(a: Float, b: Float, v0: Vector3, v1: Vector3, h: Vector3): Boolean {
        val p0 = a * v0.x - b * v0.y
        val p1 = a * v1.x - b * v1.y
        val min = minOf(p0, p1)
        val max = maxOf(p0, p1)
        val rad = Math.abs(a) * h.x + Math.abs(b) * h.y
        return min <= rad && max >= -rad
    }

    private fun planeBoxOverlap(normal: Vector3, d: Float, halfSize: Vector3): Boolean {
        val vmin = tmpVMin
        val vmax = tmpVMax
        vmin.x = if (normal.x > 0) -halfSize.x else halfSize.x
        vmax.x = if (normal.x > 0) halfSize.x else -halfSize.x
        vmin.y = if (normal.y > 0) -halfSize.y else halfSize.y
        vmax.y = if (normal.y > 0) halfSize.y else -halfSize.y
        vmin.z = if (normal.z > 0) -halfSize.z else halfSize.z
        vmax.z = if (normal.z > 0) halfSize.z else -halfSize.z
        if (normal.dot(vmin) + d > 0) return false
        if (normal.dot(vmax) + d >= 0) return true
        return false
    }
}