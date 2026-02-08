package org.roldy.core.system

import com.badlogic.gdx.math.Vector2

interface WindAttributes {
    val time: Float
    val windStrength: Float
    val windSpeed: Float
    val windDirection: Vector2
}

class WindSystem : WindAttributes {
    // Wind parameters - adjust these for different wind effects!
    override var time = 0f
    override var windStrength = 0.1f              // 0.0 = no wind, 0.5 = strong wind
    override var windSpeed = 1.0f                  // Animation speed multiplier
    override var windDirection = Vector2(1f, 0.5f) // Wind direction (X, Z)

    context(delta: Float)
    fun update() {
        // Update time for wind animation
        time += delta
    }

}