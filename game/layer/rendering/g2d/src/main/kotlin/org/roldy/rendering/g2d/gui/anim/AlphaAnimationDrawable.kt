package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.rendering.g2d.gui.DrawableDsl
import org.roldy.rendering.g2d.gui.MaskedImage
import org.roldy.rendering.g2d.gui.StackDrawable

class AlphaAnimationDrawable(
    drawable: Drawable,
    private val resolver: AnimationDrawableStateResolver,
    private val transition: Transition
) : BaseDrawable() {
    private val alphaDrawable = AlphaDrawable(drawable)

    data class Transition(
        val speed: Float = 8f,
        val colors: Map<AnimationDrawableState, Color>
    )


    private var alpha: Float = 0f
    val tmp = Color()


    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        draw(batch, x, y, width, height, 1f)
    }

    fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float, parentAlpha: Float) {
        // Determine target
        val targetColor = transition.colors[resolver.state] ?: Color.WHITE
        // Animate
        alpha =
            MathUtils.lerp(
                alpha,
                targetColor.a,
                Gdx.graphics.deltaTime * transition.speed
            )
// Draw overlay
        tmp.a = alpha * parentAlpha
        tmp.r = targetColor.r
        tmp.g = targetColor.g
        tmp.b = targetColor.b
        alphaDrawable.color = tmp
        alphaDrawable.draw(batch, x, y, width, height)
    }

    class AlphaDrawable(
        val drawable: Drawable
    ) : BaseDrawable() {
        var color: Color = Color()

        override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
            val masked = masked

            if (masked != null) {
                masked.color = color
            } else {
                batch.setColor(color.r, color.g, color.b, color.a)
            }

            drawable.draw(batch, x, y, width, height)

            batch.color = Color.WHITE
        }

        val masked by lazy {
            when (drawable) {
                is MaskedImage -> drawable
                is StackDrawable -> drawable.stack.filterIsInstance<MaskedImage>().find {
                    it.mask
                }

                else -> null
            }
        }
    }
}

@DrawableDsl
fun transition(
    speed: Float = 8f,
    colors: Map<AnimationDrawableState, Color>
) = AlphaAnimationDrawable.Transition(
    speed,
    colors
)