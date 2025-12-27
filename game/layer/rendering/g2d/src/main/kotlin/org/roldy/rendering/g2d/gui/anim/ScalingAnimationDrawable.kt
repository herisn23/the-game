package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.rendering.g2d.gui.DrawableDsl

class ScalingAnimationDrawable(
    val drawable: Drawable,
    private val resolver: AnimationDrawableStateResolver,
    private val transition: Transition
) : BaseDrawable(), AnimationDrawable {
    data class Transition(
        val speed: Float = 8f,
        val scales: Map<AnimationDrawableState, Float>
    )

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
        targetScale = transition.scales[state] ?: 1f

        // Smoothly animate current scale towards target
        if (currentScale != targetScale) {
            val diff = targetScale - currentScale
            val change = transition.speed * delta

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

@DrawableDsl
fun transition(
    speed: Float = 8f,
    scaling: Map<AnimationDrawableState, Float>
) = ScalingAnimationDrawable.Transition(
    speed,
    scaling
)