package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import org.roldy.rendering.g2d.gui.anim.core.AnimatedDrawable
import org.roldy.rendering.g2d.gui.anim.core.AnimationCallbackDsl
import org.roldy.rendering.g2d.gui.anim.core.AnimationDsl

class StackedAnimatedDrawable : BaseDrawable(), AnimatedDrawable {

    private val drawables: MutableList<AnimatedDrawable> = mutableListOf()
    override fun update(delta: Float) {
        drawables.forEach {
            it.update(delta)
        }
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        drawables.forEach {
            it.draw(batch, x, y, width, height)
        }
    }

    @AnimationCallbackDsl
    fun add(
        animation: () -> AnimatedDrawable
    ) {
        drawables.add(animation())
    }

    @AnimationCallbackDsl
    fun add(
        animation: AnimatedDrawable
    ) {
        drawables.add(animation)
    }
}

fun stackedAnimation(
    configure: @AnimationDsl StackedAnimatedDrawable.() -> Unit
) =
    StackedAnimatedDrawable().apply {
        configure()
    }