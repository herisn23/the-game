package org.roldy.core.interpolation

import com.badlogic.gdx.math.MathUtils

class FloatMap(
    values: Map<Float, Float>
) : MapInterpolation<Float>(values) {

    override fun interpolate(time: Float): Float =
        interpolate(time) { lowerValue, upperValue, t ->
            MathUtils.lerp(lowerValue, upperValue, t)
        }
}