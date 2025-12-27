package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable

class DeltaAnimationDrawable<S : AnimationDrawableState, V>(
    val drawable: AnimationDrawable<S, V>,
) : BaseDrawable() {
    override fun draw(
        batch: Batch,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        drawable.update(Gdx.graphics.deltaTime)
        drawable.draw(batch, x, y, width, height)
    }
}

fun <S : AnimationDrawableState, V> AnimationDrawable<S, V>.delta() =
    DeltaAnimationDrawable(this)