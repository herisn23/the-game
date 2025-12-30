package org.roldy.rendering.g2d.gui.anim.core

import com.badlogic.gdx.scenes.scene2d.utils.Drawable


class MixAnim(
    baseDrawable: Drawable
) {

    private var drawable: Drawable = baseDrawable


    @AnimationCallbackDsl
    fun <S : AnimationDrawableState, V> add(
        animation: (
            Drawable,
            AnimationConfiguration<S, V>.() -> Unit
        ) -> AnimatedDrawable,
        configure: @AnimationDsl AnimationConfiguration<S, V>.() -> Unit
    ) {
        drawable = animation(drawable, configure).delta()
    }

    @AnimationCallbackDsl
    fun <S> add(
        animation: (
            Drawable,
            S
        ) -> AnimatedDrawable,
        configure: @AnimationDsl S
    ) {
        drawable = animation(drawable, configure).delta()
    }

    fun drawable(): Drawable = drawable
}

fun mixAnimation(
    drawable: Drawable,
    configure: MixAnim.() -> Unit
) =
    MixAnim(drawable)
        .also(configure)
        .drawable()


