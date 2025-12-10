package org.roldy.data.map

/**
 * Data class holding noise values for a specific position
 */
data class NoiseData(
    val x: Int,
    val y: Int,
    val elevation: Float,
    val temperature: Float,
    val moisture: Float
)