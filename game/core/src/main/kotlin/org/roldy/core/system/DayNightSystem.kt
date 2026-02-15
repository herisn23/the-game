package org.roldy.core.system

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import org.roldy.core.interpolation.ColorMap
import org.roldy.core.interpolation.FloatMap

class DayNightSystem(
    private val environment: Environment,
    val sunLight: DirectionalLight,
    val moonLight: DirectionalLight,
    val ambientColorMap: ColorMap,
    val atmosphereColorMap: ColorMap,
    val sunIntensityMap: FloatMap,
    val moonIntensityMap: FloatMap,       // NEW — e.g. peak ~0.3 at midnight, 0 during day
    val moonColor: Color = Color(0.6f, 0.65f, 0.8f, 1f), // Cool blueish tint
    val dayDurationSeconds: Float = 120f
) {
    // Time: 0.0 = midnight, 0.25 = sunrise, 0.5 = noon, 0.75 = sunset, 1.0 = midnight
    var timeOfDay: Float = 0.55f // Start at sunrise
        private set

    // Sun rotation axis (tilted like Earth)
    private val sunDirection = Vector3()
    private val moonDirection = Vector3()

    // Colors for different times of day

    // Cached colors
    private var currentAtmosphereColor = Color()
    private var currentAmbientColor = Color()

    // Listeners
    var onTimeChanged: ((Float, String) -> Unit)? = null

    init {
        context(0f) {
            update()
        }
    }

    context(deltaTime: Float)
    fun update() {
        // Advance time
        timeOfDay += deltaTime / dayDurationSeconds
        if (timeOfDay >= 1f) timeOfDay -= 1f

        updateLightsDirection()
        updateColors()

        // Notify listener
        onTimeChanged?.invoke(timeOfDay, getTimeString())
    }

    private fun updateLightsDirection() {
        val sunAngle = timeOfDay * 360f - 90f

        sunDirection.set(
            MathUtils.cosDeg(sunAngle),
            -MathUtils.sinDeg(sunAngle),
            0.2f
        ).nor()
        sunLight.direction.set(sunDirection)

        // Moon is ~180° opposite the sun
        val moonAngle = sunAngle + 180f
        moonDirection.set(
            MathUtils.cosDeg(moonAngle),
            -MathUtils.sinDeg(moonAngle),
            -0.15f // Slightly different tilt than sun
        ).nor()
        moonLight.direction.set(moonDirection)
    }

    private fun updateColors() {
        // Sun
        currentAtmosphereColor = atmosphereColorMap.interpolate(timeOfDay)
        val sunIntensity = sunIntensityMap.interpolate(timeOfDay)
        sunLight.color.set(currentAtmosphereColor).mul(sunIntensity)

        // Moon
        val moonIntensity = moonIntensityMap.interpolate(timeOfDay)
        moonLight.color.set(moonColor).mul(moonIntensity)

        // Ambient — blend between day ambient and a darker night ambient
        currentAmbientColor = ambientColorMap.interpolate(timeOfDay)
        val ambientAttr = environment.get(ColorAttribute.AmbientLight) as? ColorAttribute
        ambientAttr?.color?.set(currentAmbientColor)
    }


    // Manual time control
    fun setTime(time: Float) {
        timeOfDay = time.coerceIn(0f, 1f)
        updateLightsDirection()
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
}