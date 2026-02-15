package org.roldy.core.interpolation

abstract class MapInterpolation<T>(
    val values: Map<Float, T>
) {
    val keys = values.keys.sorted()
    var lowerKey = keys.first()
    var upperKey = keys.last()

    protected fun interpolate(
        time: Float,
        interpolate: (T, T, Float) -> T
    ): T {
        for (i in 0 until keys.size - 1) {
            if (time >= keys[i] && time <= keys[i + 1]) {
                lowerKey = keys[i]
                upperKey = keys[i + 1]
                break
            }
        }

        val lowerValue = values.getValue(lowerKey)
        val upperValue = values.getValue(upperKey)

        val t = if (upperKey == lowerKey) 0f else (time - lowerKey) / (upperKey - lowerKey)
        return interpolate(lowerValue, upperValue, t)
    }

    abstract fun interpolate(time: Float): T
}