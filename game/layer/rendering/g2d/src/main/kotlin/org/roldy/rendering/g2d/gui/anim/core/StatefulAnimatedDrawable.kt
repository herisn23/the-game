package org.roldy.rendering.g2d.gui.anim.core

import com.badlogic.gdx.graphics.g2d.Batch


@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class AnimationDsl

@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class AnimationCallbackDsl

interface AnimatedDrawable {
    fun update(delta: Float)
    fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float)
}

interface StatefulAnimatedDrawable<S : AnimationDrawableState, V> : AnimatedDrawable {
    val resolver: AnimationDrawableStateResolver<S>
    val animation: AnimationConfiguration<S, V>

}

interface AnimationConfiguration<S : AnimationDrawableState, V> {
    var speed: Float
    val state: MutableMap<S, V>

    @AnimationCallbackDsl
    infix fun S.have(s: V) {
        state[this] = s
    }
}

class DefaultAnimationConfiguration<S : AnimationDrawableState, V> : AnimationConfiguration<S, V> {
    override var speed: Float = 8f
    override val state: MutableMap<S, V> = mutableMapOf()
}


