package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class KTraverseDrawable(
    val origin: Drawable,
    val child: Drawable,
) : Drawable by origin {

    override fun draw(batch: Batch?, x: Float, y: Float, width: Float, height: Float) {
        origin.draw(batch, x, y, width, height)
        child.draw(batch, x, y, width, height)
    }

    override fun setPadding(
        topHeight: Float,
        leftWidth: Float,
        bottomHeight: Float,
        rightWidth: Float
    ) {
        origin.setPadding(topHeight, leftWidth, bottomHeight, rightWidth)
    }

    override fun setPadding(padding: Float) {
        origin.setPadding(padding)
    }

    override fun setPadding(from: Drawable?) {
        origin.setPadding(from)
    }

    override fun setMinSize(minWidth: Float, minHeight: Float) {
        origin.setMinSize(minWidth, minHeight)
    }

}

@Scene2dDsl
infix fun Drawable.traverse(drawable: () -> Drawable) =
    KTraverseDrawable(this, drawable())

@Scene2dDsl
infix fun Drawable.traverse(drawable: Drawable) =
    KTraverseDrawable(this, drawable)