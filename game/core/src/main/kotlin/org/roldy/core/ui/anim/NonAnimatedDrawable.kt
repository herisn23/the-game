package org.roldy.core.ui.anim

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.copyTo

class NonAnimatedDrawable(
    val drawable: Drawable,
    color: Color,
) : org.roldy.core.ui.anim.base.AnimatedDrawable {
    private val tmp = Color()
    private val color: Color = color.cpy()

    override fun update(delta: Float) {
    }

    override fun draw(
        batch: Batch,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        batch.color copyTo tmp
        color.a = tmp.a
        batch.color = color
        drawable.draw(batch, x, y, width, height)
        batch.color = tmp
    }
}

fun noneAnimation(
    drawable: Drawable,
    color: Color = Color.WHITE
) =
    NonAnimatedDrawable(drawable, color)