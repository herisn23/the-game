package org.roldy.rendering.g2d.animation.state

import com.badlogic.gdx.math.MathUtils.lerp
import kotlin.math.sin


interface ScaleAnimationAnimationStyle {
    context(time: Float)
    fun update(): Pair<Float, Float>
}

class PulseAnimationStyle(
    val minScale: Float,
    val maxScale: Float
) : ScaleAnimationAnimationStyle {
    context(time: Float)
    override fun update(): Pair<Float, Float> {
        val progress = (sin(time) + 1f) / 2f  // 0 to 1
        val scale = lerp(minScale, maxScale, progress)
        return scale to scale
    }
}

fun pulse(minScale: Float, maxScale: Float): PulseAnimationStyle =
    PulseAnimationStyle(minScale, maxScale)

class ScaleAnimationStyle(
    val minScaleX: Float,
    val maxScaleX: Float,
    val minScaleY: Float,
    val maxScaleY: Float
) : ScaleAnimationAnimationStyle {
    context(time: Float)
    override fun update(): Pair<Float, Float> {
        val progress = (sin(time) + 1f) / 2f  // 0 to 1
        val scaleX = lerp(minScaleX, maxScaleX, progress)
        val scaleY = lerp(minScaleY, maxScaleY, progress)
        return scaleX to scaleY
    }
}

fun stretch(
    minScaleX: Float,
    maxScaleX: Float,
    minScaleY: Float,
    maxScaleY: Float
) = ScaleAnimationStyle(minScaleX, maxScaleX, minScaleY, maxScaleY)


class ScaleAnimationAnimation(
    val data: AnimationData,
    speed: Float,
    initialTime:Float,
    val animation: ScaleAnimationAnimationStyle
) : TimeBasedAnimation(speed, initialTime) {

    context(time: Float)
    override fun process() {
        val (scaleX, scaleY) = animation.update()
        data.scaleX = scaleX
        data.scaleY = scaleY
    }
}