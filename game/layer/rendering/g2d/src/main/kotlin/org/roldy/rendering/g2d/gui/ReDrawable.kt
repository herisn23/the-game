package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

typealias Redraw = (x: Float, y: Float, width: Float, height: Float, draw:(x: Float, y: Float, width: Float, height: Float) -> Unit) -> Unit

class ReDrawable(
    val drawable: Drawable,
    val redraw: Redraw
) : Drawable by drawable {

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        redraw(x, y, width, height) { x, y, width, height ->
            drawable.draw(batch, x, y, width, height)
        }
    }

    override fun setPadding(
        topHeight: Float,
        leftWidth: Float,
        bottomHeight: Float,
        rightWidth: Float
    ) {
        drawable.setPadding(topHeight, leftWidth, bottomHeight, rightWidth)
    }

    override fun setPadding(padding: Float) {
        drawable.setPadding(padding)
    }

    override fun setPadding(from: Drawable?) {
        drawable.setPadding(from)
    }

    override fun setMinSize(minWidth: Float, minHeight: Float) {
        drawable.setMinSize(minWidth, minHeight)
    }
}

@Scene2dCallbackDsl
infix fun Drawable.redraw(redraw: Redraw) =
    ReDrawable(this, redraw)