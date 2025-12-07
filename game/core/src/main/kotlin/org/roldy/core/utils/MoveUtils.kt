package org.roldy.core.utils

import com.badlogic.gdx.math.Vector2

object MoveUtils {

    // Move at constant speed (like Unity's MoveTowards)
    fun moveTowards(
        current: Vector2,
        target: Vector2,
        speed: Float,
        delta: Float,
        reached: () -> Unit = {}
    ):Vector2? {
        val moveDistance = speed * delta
        val direction = target.cpy().sub(current)
        val distance = direction.len()
        return if (distance <= moveDistance) {
            // Reached target
            reached()
            null
        } else {
            // Move towards target
            current.add(direction.nor().scl(moveDistance))
            current
        }
    }

    // Smooth damped movement (like Unity's SmoothDamp)
    fun smoothDamp(
        current: Vector2,
        target: Vector2,
        currentVelocity: Vector2,
        smoothTime: Float,
        maxSpeed: Float = Float.POSITIVE_INFINITY,
        delta: Float
    ): Vector2 {
        val omega = 2f / smoothTime
        val x = omega * delta
        val exp = 1f / (1f + x + 0.48f * x * x + 0.235f * x * x * x)

        val change = current.cpy().sub(target)
        val originalTarget = target.cpy()

        // Clamp maximum speed
        val maxChange = maxSpeed * smoothTime
        val changeLen = change.len()
        if (changeLen > maxChange) {
            change.scl(maxChange / changeLen)
        }

        val targetPos = current.cpy().sub(change)
        val temp = currentVelocity.cpy().add(change.scl(omega)).scl(delta)

        currentVelocity.set(currentVelocity.cpy().sub(change.scl(omega)).scl(exp))

        var output = targetPos.cpy().add(temp.scl(exp))

        // Prevent overshooting
        val origMinusCurrent = originalTarget.cpy().sub(current)
        val outMinusOrig = output.cpy().sub(originalTarget)

        if (origMinusCurrent.dot(outMinusOrig) > 0) {
            output = originalTarget
            currentVelocity.set(output.cpy().sub(originalTarget).scl(1f / delta))
        }

        return output
    }

    // Move with acceleration
    fun moveWithAcceleration(
        current: Vector2,
        target: Vector2,
        velocity: Vector2,
        acceleration: Float,
        maxSpeed: Float,
        delta: Float
    ): Vector2 {
        val direction = target.cpy().sub(current).nor()
        velocity.add(direction.scl(acceleration * delta))

        // Clamp to max speed
        if (velocity.len() > maxSpeed) {
            velocity.nor().scl(maxSpeed)
        }

        return current.cpy().add(velocity.cpy().scl(delta))
    }
}