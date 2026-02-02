package org.roldy.core

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3

class DayNightCycle(
    private val environment: Environment,
    private val sunLight: DirectionalLight,
    val dayDurationSeconds: Float = 120f // Real seconds for full day
) {
    // Time: 0.0 = midnight, 0.25 = sunrise, 0.5 = noon, 0.75 = sunset, 1.0 = midnight
    var timeOfDay: Float = 0.25f // Start at sunrise
        private set

    // Sun rotation axis (tilted like Earth)
    private val sunAxis = Vector3(0f, 0f, 1f)
    private val sunDirection = Vector3()

    // Colors for different times of day
    private val sunColors = mapOf(
        0.00f to Color(0.1f, 0.1f, 0.3f, 1f),    // Midnight - dark blue
        0.20f to Color(0.1f, 0.1f, 0.2f, 1f),    // Pre-dawn - very dark
        0.25f to Color(1.0f, 0.5f, 0.2f, 1f),    // Sunrise - orange
        0.30f to Color(1.0f, 0.8f, 0.6f, 1f),    // Early morning - warm
        0.50f to Color(1.0f, 0.95f, 0.9f, 1f),   // Noon - bright white
        0.70f to Color(1.0f, 0.8f, 0.6f, 1f),    // Late afternoon - warm
        0.75f to Color(1.0f, 0.4f, 0.1f, 1f),    // Sunset - deep orange
        0.80f to Color(0.3f, 0.2f, 0.4f, 1f),    // Dusk - purple
        0.85f to Color(0.1f, 0.1f, 0.3f, 1f),    // Night - dark blue
        1.00f to Color(0.1f, 0.1f, 0.3f, 1f)     // Midnight
    )

    private val ambientColors = mapOf(
        0.00f to Color(0.05f, 0.05f, 0.15f, 1f), // Midnight
        0.20f to Color(0.05f, 0.05f, 0.1f, 1f),  // Pre-dawn
        0.25f to Color(0.3f, 0.2f, 0.2f, 1f),    // Sunrise
        0.30f to Color(0.4f, 0.4f, 0.4f, 1f),    // Morning
        0.50f to Color(0.6f, 0.6f, 0.6f, 1f),    // Noon
        0.70f to Color(0.4f, 0.4f, 0.4f, 1f),    // Afternoon
        0.75f to Color(0.3f, 0.2f, 0.2f, 1f),    // Sunset
        0.80f to Color(0.15f, 0.1f, 0.2f, 1f),   // Dusk
        0.85f to Color(0.05f, 0.05f, 0.15f, 1f), // Night
        1.00f to Color(0.05f, 0.05f, 0.15f, 1f)  // Midnight
    )

    private val sunIntensities = mapOf(
        0.00f to 0.0f,   // Midnight - no sun
        0.20f to 0.0f,   // Pre-dawn
        0.25f to 0.3f,   // Sunrise
        0.30f to 0.7f,   // Morning
        0.50f to 1.0f,   // Noon - full intensity
        0.70f to 0.7f,   // Afternoon
        0.75f to 0.3f,   // Sunset
        0.80f to 0.0f,   // Dusk
        0.85f to 0.0f,   // Night
        1.00f to 0.0f    // Midnight
    )

    // Cached colors
    private val currentSunColor = Color()
    private val currentAmbientColor = Color()

    // Listeners
    var onTimeChanged: ((Float, String) -> Unit)? = null

    init {
        update(0f)
    }

    fun update(deltaTime: Float) {
        // Advance time
        timeOfDay += deltaTime / dayDurationSeconds
        if (timeOfDay >= 1f) timeOfDay -= 1f

        updateSunDirection()
        updateColors()

        // Notify listener
        onTimeChanged?.invoke(timeOfDay, getTimeString())
    }

    private fun updateSunDirection() {
        // Sun rotates 360 degrees over a full day
        // At time 0.5 (noon), sun is highest (Y = -1, pointing down)
        // At time 0.0 or 1.0 (midnight), sun is lowest (Y = 1, pointing up, below horizon)

        val angle = timeOfDay * 360f - 90f // -90 so noon is at top

        sunDirection.set(
            MathUtils.cosDeg(angle),
            -MathUtils.sinDeg(angle),
            0.2f // Slight tilt for more interesting shadows
        ).nor()

        sunLight.direction.set(sunDirection)
    }

    private fun updateColors() {
        // Interpolate sun color
        interpolateColor(sunColors, timeOfDay, currentSunColor)
        val intensity = interpolateValue(sunIntensities, timeOfDay)

        sunLight.color.set(currentSunColor).mul(intensity)

        // Interpolate ambient color
        interpolateColor(ambientColors, timeOfDay, currentAmbientColor)

        val ambientAttr = environment.get(ColorAttribute.AmbientLight) as? ColorAttribute
        ambientAttr?.color?.set(currentAmbientColor)
    }

    private fun interpolateColor(colorMap: Map<Float, Color>, time: Float, out: Color): Color {
        val keys = colorMap.keys.sorted()

        var lowerKey = keys.first()
        var upperKey = keys.last()

        for (i in 0 until keys.size - 1) {
            if (time >= keys[i] && time <= keys[i + 1]) {
                lowerKey = keys[i]
                upperKey = keys[i + 1]
                break
            }
        }

        val lowerColor = colorMap[lowerKey]!!
        val upperColor = colorMap[upperKey]!!

        val t = if (upperKey == lowerKey) 0f else (time - lowerKey) / (upperKey - lowerKey)

        out.r = MathUtils.lerp(lowerColor.r, upperColor.r, t)
        out.g = MathUtils.lerp(lowerColor.g, upperColor.g, t)
        out.b = MathUtils.lerp(lowerColor.b, upperColor.b, t)
        out.a = 1f

        return out
    }

    private fun interpolateValue(valueMap: Map<Float, Float>, time: Float): Float {
        val keys = valueMap.keys.sorted()

        var lowerKey = keys.first()
        var upperKey = keys.last()

        for (i in 0 until keys.size - 1) {
            if (time >= keys[i] && time <= keys[i + 1]) {
                lowerKey = keys[i]
                upperKey = keys[i + 1]
                break
            }
        }

        val lowerValue = valueMap[lowerKey]!!
        val upperValue = valueMap[upperKey]!!

        val t = if (upperKey == lowerKey) 0f else (time - lowerKey) / (upperKey - lowerKey)

        return MathUtils.lerp(lowerValue, upperValue, t)
    }

    // Manual time control
    fun setTime(time: Float) {
        timeOfDay = time.coerceIn(0f, 1f)
        updateSunDirection()
        updateColors()
    }

    fun setTimeHours(hours: Float) {
        setTime(hours / 24f)
    }

    // Utility
    fun getTimeString(): String {
        val totalMinutes = (timeOfDay * 24 * 60).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "%02d:%02d".format(hours, minutes)
    }

    fun getHours(): Float = timeOfDay * 24f

    fun isDay(): Boolean = timeOfDay in 0.25f..0.75f

    fun isNight(): Boolean = !isDay()

    fun isSunrise(): Boolean = timeOfDay in 0.23f..0.30f

    fun isSunset(): Boolean = timeOfDay in 0.73f..0.80f

    fun getSunIntensity(): Float = interpolateValue(sunIntensities, timeOfDay)

    fun getSunColor(): Color = currentSunColor

    fun getAmbientColor(): Color = currentAmbientColor
}