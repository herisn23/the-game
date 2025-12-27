package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.copyTo
import org.roldy.rendering.g2d.gui.DrawableDsl
import org.roldy.rendering.g2d.gui.MaskedImage

class AlphaAnimatedDrawable<S : AnimationDrawableState>(
    override val drawable: Drawable,
    override val resolver: AnimationDrawableStateResolver,
    override val animation: AlphaAnimationConfiguration<S>
) : BaseDrawable(), ConfigurableAnimatedDrawable<S, Float> {
    private val color = animation.color.cpy()
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
        val targetAlpha = animation.config[resolver.state] ?: 1f

        // Animate alpha towards target
        alpha = MathUtils.lerp(alpha, targetAlpha, delta * animation.speed)
        color.a = alpha
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        batch.color copyTo batchColor
        if (mask != null) {
            mask?.color = color
        } else {
            batch.color = color
        }
        drawable.draw(batch, x, y, width, height)
        batch.color = batchColor
    }
}

class AlphaAnimationConfiguration<S : AnimationDrawableState> : AnimationConfiguration<S, Float> {
    override var speed: Float = 8f
    override val config: MutableMap<S, Float> = mutableMapOf()
    var color: Color = Color.WHITE
}

fun <S : AnimationDrawableState> AnimationDrawableStateResolver.alpha(
    drawable: Drawable,
    configure: @DrawableDsl AlphaAnimationConfiguration<S>.() -> Unit
) =
    AlphaAnimatedDrawable(
        drawable,
        this,
        AlphaAnimationConfiguration<S>().apply {
            configure()
        }
    )