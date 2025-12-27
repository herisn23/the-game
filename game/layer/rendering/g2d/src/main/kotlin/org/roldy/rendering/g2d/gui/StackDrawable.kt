package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class StackDrawable : BaseDrawable() {
    private val drawables: MutableList<Drawable> = mutableListOf()

    fun add(drawable: Drawable) {
        drawables.add(drawable)
    }

    val stack: List<Drawable> get() = drawables

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        drawables.forEach { it.draw(batch, x, y, width, height) }
    }


}

@DrawableDsl
fun stackDrawable(
    init: @DrawableDsl StackDrawable.() -> Unit
): StackDrawable =
    StackDrawable().apply {
        init()
    }

@DrawableDsl
fun StackDrawable.drawable(drawable: () -> Drawable) {
    add(drawable())
}