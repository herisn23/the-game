package org.roldy.core.system

import com.badlogic.gdx.math.Vector3

class WindSystem {
    // Wind parameters - adjust these for different wind effects!
    var time = 0f
    var windDirection = Vector3(1f, 0f, 0f)
    var windIntensity = 0.1f//0.801f
    var weatherIntensity = 0.082f

    context(delta: Float)
    fun update() {
        // Update time for wind animation
        time += delta// * 0.5f
    }

}