package org.roldy.data.configuration

import org.roldy.data.configuration.FloatComparison.FloatComparator.Greater
import org.roldy.data.configuration.FloatComparison.FloatComparator.Lesser
import org.roldy.data.map.NoiseData

data class FloatComparison(
    val value: Float,
    val equality: FloatComparator = Lesser
) {
    enum class FloatComparator(
        val char: String,
    ) {
        Lesser("<"), Greater(">")
    }
}

interface HeightData {
    val elevation: FloatComparison
    val temperature: FloatComparison
    val moisture: FloatComparison
}

infix fun FloatComparison.match(value: Float): Boolean =
    when (equality) {
        Lesser -> value <= this.value
        Greater -> value >= this.value
    }

infix fun HeightData.match(noiseData: NoiseData) =
    elevation.match(noiseData.elevation) &&
            temperature.match(noiseData.temperature) &&
            moisture.match(noiseData.moisture)
