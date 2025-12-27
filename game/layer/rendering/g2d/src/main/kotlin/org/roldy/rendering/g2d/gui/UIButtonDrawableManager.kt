package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import org.roldy.rendering.g2d.gui.anim.AlphaAnimationDrawable

class UIButtonDrawableManager(
    val background: AlphaAnimationDrawable,
    val button: Button
) {
    fun draw(batch: Batch, parentAlpha: Float) {
        // Determine target
        background.draw(batch, button.x, button.y, button.width, button.height, parentAlpha)
    }
}