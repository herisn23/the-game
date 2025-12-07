package org.roldy.core.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import kotlin.math.atan2

object OrientationUtils {

    // Get angle in degrees (0° = right, 90° = up, 180° = left, 270° = down)
    fun getAngleTo(from: Vector2, to: Vector2): Float {
        val dx = to.x - from.x
        val dy = to.y - from.y
        return atan2(dy, dx) * MathUtils.radiansToDegrees
    }

    // Get normalized direction vector
    fun getDirectionTo(from: Vector2, to: Vector2): Vector2 {
        return to.cpy().sub(from).nor()
    }

    // Get angle in radians
    fun getAngleToRadians(from: Vector2, to: Vector2): Float {
        val dx = to.x - from.x
        val dy = to.y - from.y
        return atan2(dy, dx)
    }
}