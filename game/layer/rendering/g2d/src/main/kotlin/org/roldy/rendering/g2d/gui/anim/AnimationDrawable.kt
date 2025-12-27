package org.roldy.rendering.g2d.gui.anim

import com.badlogic.gdx.graphics.g2d.Batch

interface AnimationDrawable {
    fun update(delta: Float)
    fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float)
}