package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.scenes.scene2d.utils.Drawable


data class MixAnim(
    private val resolver: AnimationDrawableStateResolver,
    private val baseDrawable: Drawable,
) {


}

infix fun AnimationDrawableStateResolver.mix(drawable: Drawable) =
    MixAnim(this, drawable)


