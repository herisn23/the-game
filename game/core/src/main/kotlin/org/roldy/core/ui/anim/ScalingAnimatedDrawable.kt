package org.roldy.core.ui.anim

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.ui.anim.base.AnimationDrawableState
import org.roldy.core.ui.anim.base.AnimationDrawableStateResolver
import org.roldy.core.ui.anim.base.DefaultAnimationConfiguration
import org.roldy.core.ui.anim.base.StatefulAnimatedDrawable

class ScalingAnimatedDrawable<S : AnimationDrawableState>(
    val drawable: Drawable,
    override val resolver: AnimationDrawableStateResolver<S>,
    override val animation: DefaultAnimationConfiguration<S, Float>
) : BaseDrawable(), StatefulAnimatedDrawable<S, Float> {
    private var currentScale: Float = 1f
    private var targetScale: Float = 1f

    /**
     * Updates the scale animation. Should be called every frame.
     * @param delta Time elapsed since last update in seconds
     */
    override fun update(delta: Float) {
        // Get current state from resolver
        val state = resolver.state

        // Get target scale for current state (default to 1.0 if not defined)
        targetScale = animation.state[state] ?: 1f

        // Smoothly animate current scale towards target
        if (currentScale != targetScale) {
            val diff = targetScale - currentScale
            val change = animation.speed * delta

            currentScale = when {
                kotlin.math.abs(diff) <= change -> targetScale
                diff > 0 -> currentScale + change
                else -> currentScale - change
            }
        }
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        // Calculate scaled dimensions
        val scaledWidth = width * currentScale
        val scaledHeight = height * currentScale

        // Center the scaled drawable within the original bounds
        val offsetX = (width - scaledWidth) / 2f
        val offsetY = (height - scaledHeight) / 2f

        // Draw the scaled drawable
        drawable.draw(batch, x + offsetX, y + offsetY, scaledWidth, scaledHeight)
    }
}

fun <S : AnimationDrawableState> AnimationDrawableStateResolver<S>.scaleAnimation(
    drawable: Drawable,
    configure: @org.roldy.core.ui.anim.base.AnimationDsl DefaultAnimationConfiguration<S, Float>.() -> Unit
) =
    ScalingAnimatedDrawable(
        drawable,
        this,
        DefaultAnimationConfiguration<S, Float>().apply {
            configure()
        }
    )