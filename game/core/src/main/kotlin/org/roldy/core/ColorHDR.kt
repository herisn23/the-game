package org.roldy.core

import com.badlogic.gdx.math.Vector3
import kotlin.math.pow

class ColorHDR(
    r: Float,
    g: Float,
    b: Float,
) {
    val intensity: Float = maxOf(r, g, b, 1.0f)
    val hdr: Vector3 = Vector3(r / intensity, g / intensity, b / intensity)
    val raw: Vector3 = Vector3(r, g, b)
    val rgb: Vector3 = reinhardToneMap()

    fun reinhardToneMap(): Vector3 {
        val arr = FloatArray(3)
        arr[0] = hdr.x
        arr[1] = hdr.y
        arr[2] = hdr.z

        // Apply Reinhard operator: x / (1 + x)
        val mapped = arr.map { it / (1f + it) }

        // Gamma correction (2.2)
        val gammaCorrected = mapped.map { it.toDouble().pow(1.0 / 2.2).toFloat() }

        return Vector3(
            gammaCorrected[0].coerceIn(0f, 1f),
            gammaCorrected[1].coerceIn(0f, 1f),
            gammaCorrected[2].coerceIn(0f, 1f)
        )
    }
}