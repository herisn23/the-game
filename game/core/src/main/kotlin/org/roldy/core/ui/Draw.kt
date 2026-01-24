package org.roldy.core.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable


typealias BatchDraw = Batch.(x: Float, y: Float, width: Float, height: Float) -> Unit

fun interface Draw {
    fun draw(x: Float, y: Float, width: Float, height: Float)
}


@DrawableDsl
fun drawable(draw: BatchDraw) =
    object : BaseDrawable() {
        override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
            batch.draw(x, y, width, height)
        }
    }