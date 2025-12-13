package org.roldy.core

class TimeManager {
    var timeScale = 1.0f

    fun getDelta(delta: Float) = delta * timeScale

    fun pause() { timeScale = 0f }
    fun slowMotion() { timeScale = 0.5f }
    fun fastForward() { timeScale = 2.0f }
    fun normalSpeed() { timeScale = 1.0f }
}