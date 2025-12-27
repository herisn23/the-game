package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.rendering.g2d.gui.DrawableDsl


class MixAnim(
    baseDrawable: Drawable
) {

    private var drawable: Drawable = baseDrawable


    @AnimationDsl
    fun <S : AnimationDrawableState, V> add(
        animation: (
            Drawable,
            AnimationConfiguration<S, V>.() -> Unit
        ) -> AnimationDrawable,
        configure: @AnimationDsl AnimationConfiguration<S, V>.() -> Unit
    ) {
        drawable = animation(drawable, configure).delta()
    }

    @AnimationDsl
    fun <S> add(
        animation: (
            Drawable,
            S
        ) -> AnimationDrawable,
        configure: @DrawableDsl S
    ) {
        drawable = animation(drawable, configure).delta()
    }

    fun drawable(): Drawable = drawable
}

fun mix(
    drawable: Drawable,
    configure: MixAnim.() -> Unit
) =
    MixAnim(drawable)
        .also(configure)
        .drawable()


