package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.scenes.scene2d.utils.Drawable


fun AnimationDrawableStateResolver.scale(
    drawable: Drawable,
    transition: ScalingAnimationDrawable.Transition
) =
    ScalingAnimationDrawable(
        drawable,
        this,
        transition
    )

fun AnimationDrawableStateResolver.alpha(
    drawable: Drawable,
    transition: AlphaAnimationDrawable.Transition
) =
    AlphaAnimationDrawable(
        drawable,
        this,
        transition
    )

data class MixAnim(
    private val resolver: AnimationDrawableStateResolver,
    private val baseDrawable: Drawable,
) {


}

infix fun AnimationDrawableStateResolver.mix(drawable: Drawable) =
    MixAnim(this, drawable)


fun AnimationDrawable.delta() =
    DeltaAnimationDrawable(this)