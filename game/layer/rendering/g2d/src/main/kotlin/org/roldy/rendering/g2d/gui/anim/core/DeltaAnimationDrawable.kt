package org.roldy.rendering.g2d.gui.anim.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class DeltaAnimationDrawable(
    val drawable: AnimatedDrawable,
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

fun AnimatedDrawable.delta(): Drawable =
    DeltaAnimationDrawable(this)