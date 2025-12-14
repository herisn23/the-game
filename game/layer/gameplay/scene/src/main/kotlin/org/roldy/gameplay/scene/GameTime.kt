package org.roldy.gameplay.scene

class GameTime {
    private var totalTime = 0f

    fun update(delta: Float) {
        totalTime += delta
    }

    val totalSeconds get() = totalTime
    val totalMinutes get() = totalTime / 60f
    val totalHours get() = totalTime / 3600f

    fun reset() {
        totalTime = 0f
    }

    val formattedTime
        get(): String {
            val hours = (totalTime / 3600).toInt()
            val minutes = ((totalTime % 3600) / 60).toInt()
            val seconds = (totalTime % 60).toInt()
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
}