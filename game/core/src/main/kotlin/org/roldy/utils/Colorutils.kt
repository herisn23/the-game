package org.roldy.utils

import com.badlogic.gdx.graphics.Color

fun adjustBrightness(baseColor: Color, factor: Float): Color {
    return Color(
        baseColor.r * factor,
        baseColor.g * factor,
        baseColor.b * factor,
        baseColor.a  // Keep alpha unchanged
    )
}