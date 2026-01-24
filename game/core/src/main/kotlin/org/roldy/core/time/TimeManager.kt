package org.roldy.core.time

class TimeManager(
    val initialScale: Float = 1f,
) {
    var timeScale = initialScale

    fun getDelta(delta: Float) = delta * timeScale

    fun pause() {
        timeScale = 0f
    }

    fun slowMotion() {
        timeScale = 0.5f
    }

    fun fastForward() {
        timeScale = 2.0f
    }

    fun normalSpeed() {
        timeScale = 1.0f
    }
}