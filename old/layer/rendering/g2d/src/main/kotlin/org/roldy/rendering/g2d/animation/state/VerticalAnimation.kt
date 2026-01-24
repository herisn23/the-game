package org.roldy.rendering.g2d.animation.state

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils

interface VerticalAnimationStyle {
    context(time: Float)
    fun update(): Float
}

class BouncingVerticalAnimationStyle(
    val start: Float,
    val end: Float,
) : VerticalAnimationStyle {

    context(time: Float)
    override fun update(): Float {
        val progress = (time % 1f)
        return Interpolation.bounceOut.apply(start, end, progress)
    }

}

class DefaultVerticalAnimationStyle(val amplitude: Float) : VerticalAnimationStyle {
    context(time: Float)
    override fun update(): Float =
        MathUtils.sin(time) * amplitude

}

fun bounce(start: Float = 0f, end: Float = 5f) = BouncingVerticalAnimationStyle(start, end)


fun float(amplitude: Float = 2f) = DefaultVerticalAnimationStyle(amplitude)


class VerticalAnimation(
    val data: AnimationData,
    speed: Float,
    initialTime:Float,
    val style: VerticalAnimationStyle
) : TimeBasedAnimation(speed, initialTime) {

    context(time: Float)
    override fun process() {
        data.y += style.update()
    }
}