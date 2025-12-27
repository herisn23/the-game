package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.rendering.g2d.gui.DrawableDsl
import org.roldy.rendering.g2d.gui.MaskedImage

class AlphaAnimationDrawable(
    val drawable: Drawable,
    private val resolver: AnimationDrawableStateResolver,
    private val transition: Transition
) : BaseDrawable(), AnimationDrawable {

    val mask by lazy {
        when (drawable) {
            is MaskedImage -> drawable
            else -> null
        }
    }

    data class Transition(
        val speed: Float = 8f,
        val colors: Map<AnimationDrawableState, Color>
    )

    private var alpha: Float = 0f
    private val tmp = Color()

    override fun update(delta: Float) {
        // Determine target color based on current state
        val targetColor = transition.colors[resolver.state] ?: Color.WHITE

        // Animate alpha towards target
        alpha = MathUtils.lerp(alpha, targetColor.a, delta * transition.speed)

        // Update color components
        tmp.r = targetColor.r
        tmp.g = targetColor.g
        tmp.b = targetColor.b
        tmp.a = alpha
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        val prevColor = batch.color.cpy()
        batch.setColor(tmp.r, tmp.g, tmp.b, tmp.a)
        if (mask != null) {
            mask?.color = tmp
        } else {
            batch.setColor(tmp.r, tmp.g, tmp.b, tmp.a)
        }
        drawable.draw(batch, x, y, width, height)
        batch.setColor(prevColor.r, prevColor.g, prevColor.b, prevColor.a)
    }
}

@DrawableDsl
fun transition(
    colors: Map<AnimationDrawableState, Color>,
    speed: Float = 8f
) = AlphaAnimationDrawable.Transition(
    speed,
    colors
)