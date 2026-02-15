package org.roldy.core.interpolation

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils

class ColorMap(
    colors: Map<Float, Color>
) : MapInterpolation<Color>(colors) {

    private val tmpColor = Color()

    override fun interpolate(time: Float) =
        interpolate(time) { lower: Color, upper: Color, time: Float ->
            tmpColor.apply {
                r = MathUtils.lerp(lower.r, upper.r, time)
                g = MathUtils.lerp(lower.g, upper.g, time)
                b = MathUtils.lerp(lower.b, upper.b, time)
                a = 1f
            }
        }
}