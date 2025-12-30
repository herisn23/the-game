package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.copyTo
import org.roldy.rendering.g2d.gui.MaskedImage
import org.roldy.rendering.g2d.gui.anim.core.*

class ColorAnimatedDrawable<S : AnimationDrawableState>(
    val drawable: Drawable,
    override val resolver: AnimationDrawableStateResolver<S>,
    override val animation: ColoredAnimationConfiguration<S>
) : BaseDrawable(), StatefulAnimatedDrawable<S, Color> {
    private val tmp = Color()
    val mask by lazy {
        when (drawable) {
            is MaskedImage -> drawable
            else -> null
        }
    }

    private var alpha: Float = 0f
    private var batchColor = Color()
    override fun update(delta: Float) {
        // Determine target color based on current state
        val targetColor = animation.state[resolver.state] ?: animation.color
        tmp.r = targetColor.r
        tmp.g = targetColor.g
        tmp.b = targetColor.b
        // Animate alpha towards target
        alpha = MathUtils.lerp(alpha, targetColor.a, delta * animation.speed)
        tmp.a = alpha
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        batch.color copyTo batchColor
        tmp.a *= batchColor.a
        if (mask != null) {
            mask?.color = tmp
        } else {
            batch.color = tmp
        }
        drawable.draw(batch, x, y, width, height)
        batch.color = batchColor
    }
}


class ColoredAnimationConfiguration<S : AnimationDrawableState> : AnimationConfiguration<S, Color> {
    override var speed: Float = 8f
    override val state: MutableMap<S, Color> = mutableMapOf()
    var color: Color = Color.WHITE
}

fun <S : AnimationDrawableState> AnimationDrawableStateResolver<S>.colorAnimation(
    drawable: Drawable,
    configure: @AnimationDsl ColoredAnimationConfiguration<S>.() -> Unit = {}
) =
    ColorAnimatedDrawable(
        drawable,
        this,
        ColoredAnimationConfiguration<S>().apply {
            configure()
        }
    )