package org.roldy.core

import com.badlogic.gdx.math.Vector3

class ColorHDR(
    r: Float,
    g: Float,
    b: Float
) {
    val intensity: Float = maxOf(r, g, b, 1.0f)
    val base: Vector3 = Vector3(r / intensity, g / intensity, b / intensity)
}