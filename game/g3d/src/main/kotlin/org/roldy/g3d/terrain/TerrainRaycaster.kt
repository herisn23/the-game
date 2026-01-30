package org.roldy.g3d.terrain

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray

class TerrainRaycaster(
    private val heightSampler: TerrainHeightSampler,
    private val camera: Camera
) {
    private val ray = Ray()
    private val tmpVec = Vector3()

    fun pickTerrain(screenX: Float, screenY: Float): Vector3? {
        ray.set(camera.getPickRay(screenX, screenY))

        val maxDistance = camera.far
        val stepSize = maxDistance / 1000f

        var distance = camera.near
        val lastPoint = Vector3(ray.origin)

        while (distance < maxDistance) {
            distance += stepSize
            tmpVec.set(ray.direction).scl(distance).add(ray.origin)

            if (!heightSampler.isInBounds(tmpVec.x, tmpVec.z)) {
                lastPoint.set(tmpVec)
                continue
            }

            val terrainHeight = heightSampler.getHeightAt(tmpVec.x, tmpVec.z)

            // Ray went below terrain
            if (tmpVec.y <= terrainHeight) {
                return refineIntersection(lastPoint, tmpVec)
            }

            lastPoint.set(tmpVec)
        }

        return null
    }

    private fun refineIntersection(above: Vector3, below: Vector3): Vector3 {
        val mid = Vector3()
        val a = Vector3(above)
        val b = Vector3(below)

        repeat(10) {
            mid.set(a).add(b).scl(0.5f)
            val terrainHeight = heightSampler.getHeightAt(mid.x, mid.z)

            if (mid.y > terrainHeight) {
                a.set(mid)
            } else {
                b.set(mid)
            }
        }

        mid.y = heightSampler.getHeightAt(mid.x, mid.z)
        return mid
    }
}