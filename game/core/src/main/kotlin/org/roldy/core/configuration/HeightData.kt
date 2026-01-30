package org.roldy.core.configuration

import org.roldy.core.configuration.FloatComparison.FloatComparator.Greater
import org.roldy.core.configuration.FloatComparison.FloatComparator.Lesser
import org.roldy.core.map.NoiseData

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
    val elevation: ClosedFloatingPointRange<Float>
    val temperature: ClosedFloatingPointRange<Float>
    val moisture: ClosedFloatingPointRange<Float>
}

infix fun FloatComparison.match(value: Float): Boolean =
    when (equality) {
        Lesser -> value <= this.value
        Greater -> value >= this.value
    }

infix fun HeightData.match(noiseData: NoiseData) =
    noiseData.elevation in elevation &&
            noiseData.temperature in temperature &&
            noiseData.moisture in moisture
