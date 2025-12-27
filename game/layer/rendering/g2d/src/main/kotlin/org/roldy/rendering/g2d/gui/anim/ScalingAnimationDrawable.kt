package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.rendering.g2d.gui.DrawableDsl

class ScalingAnimationDrawable(
    val drawable: Drawable,
    private val resolver: AnimationDrawableStateResolver,
    private val transition: Transition
) : BaseDrawable() {
    data class Transition(
        val speed: Float = 8f,
        val colors: Map<AnimationDrawableState, Float>
    )

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        drawable.draw(batch, x, y, width, height)
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