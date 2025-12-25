package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha

class KButtonManager(
    val background: KButtonOverlayDrawable,
    val button: Button
) {
    private var alpha: Float = 0f
    val tmp = Color()
    fun draw(batch: Batch, parentAlpha: Float) {
        // Determine target
        val targetColor = when {
            button.isPressed -> background.transition.pressedColor
            button.isOver -> background.transition.overColor
            else -> background.transition.normalColor
        }

        // Animate
        alpha =
            MathUtils.lerp(
                alpha,
                targetColor.a,
                Gdx.graphics.deltaTime * background.transition.speed
            )
// Draw overlay
        tmp.a = alpha * parentAlpha
        tmp.r = targetColor.r
        tmp.g = targetColor.g
        tmp.b = targetColor.b
        background.color = tmp
        background.draw(batch, button.x, button.y, button.width, button.height)
    }
}

class KButtonOverlayDrawable(
    val drawable: Drawable,
    val transition: Transition = Transition()
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
            is KMaskedImage -> drawable
            is KStackDrawable -> drawable.stack.filterIsInstance<KMaskedImage>().find {
                it.mask
            }

            else -> null
        }
    }

    data class Transition(
        val speed: Float = 8f,
        val normalColor: Color = Color.WHITE alpha 0f,
        val pressedColor: Color = Color.WHITE alpha .25f,
        val overColor: Color = Color.WHITE alpha .5f
    ) {
    }
}

@DrawableDsl
fun buttonDrawable(drawable:Drawable, transition: KButtonOverlayDrawable.Transition = KButtonOverlayDrawable.Transition()) =
    KButtonOverlayDrawable(drawable, transition)

@DrawableDsl
fun transition(
    speed: Float = 8f,
    normalColor: Color,
    pressedColor: Color,
    overColor: Color
) = KButtonOverlayDrawable.Transition(speed, normalColor, pressedColor, overColor)