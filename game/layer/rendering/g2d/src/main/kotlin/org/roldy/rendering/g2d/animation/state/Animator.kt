package org.roldy.rendering.g2d.animation.state

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.rendering.g2d.Pivot

@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class Animating

class AnimationData(
    var x: Float = 0f,
    var y: Float = 0f,
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    var alpha: Float = 1f
)

interface Animation {
    context(delta: Float)
    fun update()
}

abstract class TimeBasedAnimation(
    val speed: Float,
    initialTime: Float
) : Animation {
    private var time: Float = initialTime

    context(delta: Float)
    override fun update() {
        time += delta * speed
        context(time) {
            process()
        }
    }

    context(time: Float)
    abstract fun process()
}


class Animator(
    val data: AnimationData,
    val animations: List<Animation>
) {
    context(delta: Float)
    fun update() {
        animations.forEach { it.update() }
    }

    fun render(region: TextureRegion, batch: SpriteBatch, pivot: Pivot) {
        batch.draw(
            region,
            pivot.x + data.x,
            pivot.y + data.y,
            pivot.width * data.scaleX,
            pivot.height * data.scaleY
        )
    }

}

class AnimationStack(
    val data: AnimationData,
) {
    val animations: MutableList<Animation> = mutableListOf()

    @Animating
    fun scale(
        speed: Float,
        initialTime: Float = 0f,
        animation: PulseAnimationStyle
    ) = add(ScaleAnimationAnimation(data, speed, initialTime, animation))

    @Animating
    fun vertical(
        speed: Float,
        initialTime: Float = 0f,
        animation: VerticalAnimationStyle
    ) = add(VerticalAnimation(data, speed, initialTime, animation))


    @Animating
    fun add(animation: Animation) = animations.add(animation)

}

@Animating
fun AnimationData.animation(configure: AnimationStack.() -> Unit) =
    Animator(this, AnimationStack(this).also(configure).animations)

@Animating
fun animation(configure: AnimationStack.() -> Unit) =
    AnimationData().animation(configure)