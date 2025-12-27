package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable

class DeltaAnimationDrawable(
    val drawable: AnimationDrawable,
) : BaseDrawable() {
    override fun draw(
        batch: Batch,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        draw(batch, x, y, width, height, 1f)
    }

    fun draw(
        batch: Batch,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        parentAlpha: Float,
    ) {
        drawable.update(Gdx.graphics.deltaTime)
        drawable.draw(batch, x, y, width, height)
    }
}