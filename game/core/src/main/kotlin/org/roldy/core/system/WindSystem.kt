package org.roldy.core.system

import com.badlogic.gdx.math.Vector2

class WindSystem {
    // Wind parameters - adjust these for different wind effects!
    var time = 0f
    var windStrength = 0.1f              // 0.0 = no wind, 0.5 = strong wind
    var windSpeed = 1.0f                  // Animation speed multiplier
    var windDirection = Vector2(1f, 0.5f) // Wind direction (X, Z)

    context(delta: Float)
    fun update() {
        // Update time for wind animation
        time += delta
    }

}