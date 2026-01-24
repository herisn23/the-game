package org.roldy.core.ui.anim

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.ui.anim.base.AnimationDrawableState
import org.roldy.core.ui.anim.base.AnimationDrawableStateResolver
import org.roldy.core.utils.copyTo

class ColorAnimatedDrawable<S : AnimationDrawableState>(
    val drawable: Drawable,
    override val resolver: AnimationDrawableStateResolver<S>,
    override val animation: ColoredAnimationConfiguration<S>
) : BaseDrawable(), org.roldy.core.ui.anim.base.StatefulAnimatedDrawable<S, Color> {
    private val tmp = Color()
    val mask by lazy {
        when (drawable) {
            is org.roldy.core.ui.MaskedImage -> drawable
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


class ColoredAnimationConfiguration<S : AnimationDrawableState> :
    org.roldy.core.ui.anim.base.AnimationConfiguration<S, Color> {
    override var speed: Float = 8f
    override val state: MutableMap<S, Color> = mutableMapOf()
    var color: Color = Color.WHITE
}

fun <S : AnimationDrawableState> AnimationDrawableStateResolver<S>.colorAnimation(
    drawable: Drawable,
    configure: @org.roldy.core.ui.anim.base.AnimationDsl ColoredAnimationConfiguration<S>.() -> Unit = {}
) =
    ColorAnimatedDrawable(
        drawable,
        this,
        ColoredAnimationConfiguration<S>().apply {
            configure()
        }
    )