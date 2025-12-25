package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class KStackDrawable : BaseDrawable() {
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
    init: @DrawableDsl KStackDrawable.() -> Unit
): KStackDrawable =
    KStackDrawable().apply {
        init()
    }

@DrawableDsl
fun KStackDrawable.drawable(drawable: () -> Drawable) {
    add(drawable())
}