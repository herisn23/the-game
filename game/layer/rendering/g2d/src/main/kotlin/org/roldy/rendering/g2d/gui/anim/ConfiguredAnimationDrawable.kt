package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.Drawable


@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class AnimationDsl

interface AnimationDrawable {
    val drawable: Drawable
    fun update(delta: Float)
    fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float)
}

interface ConfiguredAnimationDrawable<S : AnimationDrawableState, V> : AnimationDrawable {
    val resolver: AnimationDrawableStateResolver
    val animation: AnimationConfiguration<S, V>

}

interface AnimationConfiguration<S : AnimationDrawableState, V> {
    var speed: Float
    val config: MutableMap<S, V>

    @AnimationDsl
    infix fun S.to(s: V) {
        config[this] = s
    }
}

class DefaultAnimationConfiguration<S : AnimationDrawableState, V> : AnimationConfiguration<S, V> {
    override var speed: Float = 8f
    override val config: MutableMap<S, V> = mutableMapOf()
}


