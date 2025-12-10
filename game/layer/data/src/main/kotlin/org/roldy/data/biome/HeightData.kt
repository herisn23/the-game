package org.roldy.data.biome

import org.roldy.data.biome.FloatComparison.FloatComparator.Greater
import org.roldy.data.biome.FloatComparison.FloatComparator.Lesser


data class FloatComparison(
    val value: Float,
    val equality: FloatComparator = FloatComparator.Lesser
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

infix fun FloatComparison.match(value: Float):Boolean =
    when (equality) {
        Lesser -> value <= this.value
        Greater -> value >= this.value
    }