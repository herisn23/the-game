package org.roldy.core.ui.anim

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.ui.anim.base.AnimationConfiguration
import org.roldy.core.ui.anim.base.AnimationDrawableState
import org.roldy.core.ui.anim.base.AnimationDrawableStateResolver
import org.roldy.core.ui.anim.base.StatefulAnimatedDrawable
import org.roldy.core.utils.alpha

class AlphaAnimatedDrawable<S : AnimationDrawableState>(
    val drawable: Drawable,
    override val resolver: AnimationDrawableStateResolver<S>,
    override val animation: AlphaAnimationConfiguration<S>
) : BaseDrawable(), StatefulAnimatedDrawable<S, Float> {
    val colorDrawable = resolver.colorAnimation(drawable) {
        color = animation.color
        animation.state.forEach { (s, float) ->
            state[s] = color alpha float
        }
    }

    override fun update(delta: Float) {
        colorDrawable.update(delta)
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        colorDrawable.draw(batch, x, y, width, height)
    }
}


class AlphaAnimationConfiguration<S : AnimationDrawableState> :
    AnimationConfiguration<S, Float> {
    override var speed: Float = 8f
    override val state: MutableMap<S, Float> = mutableMapOf()
    var color: Color = Color.WHITE
}

fun <S : AnimationDrawableState> AnimationDrawableStateResolver<S>.alphaAnimation(
    drawable: Drawable,
    configure: AlphaAnimationConfiguration<S>.() -> Unit
) =
    AlphaAnimatedDrawable(
        drawable,
        this,
        AlphaAnimationConfiguration<S>().apply {
            configure()
        }
    )