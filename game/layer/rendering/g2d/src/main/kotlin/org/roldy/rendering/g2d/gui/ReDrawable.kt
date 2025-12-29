package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

typealias Redraw = Batch.(x: Float, y: Float, width: Float, height: Float, draw: BatchDraw) -> Unit

@ReDrawableDsl
infix fun Drawable.redraw(redraw: Redraw) =
    object : BaseDrawable() {
        val drawable = this@redraw
        override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
            batch.redraw(x, y, width, height) { x, y, width, height ->
                drawable.draw(batch, x, y, width, height)
            }
        }
    }