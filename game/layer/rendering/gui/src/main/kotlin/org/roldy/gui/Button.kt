package org.roldy.gui

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.TextButton

class Button(
    text: String,
    style: TextButtonStyle,
    hitMargin: Float = 0f,
    private val marginLeft: Float = hitMargin,
    private val marginRight: Float = hitMargin,
    private val marginTop: Float = hitMargin,
    private val marginBottom: Float = hitMargin,
) : TextButton(text, style) {

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (touchable && this.touchable != Touchable.enabled) return null
        if (!isVisible) return null
        val hitBox = Rectangle(
            marginLeft,
            marginBottom,
            width - marginLeft - marginRight,
            height - marginBottom - marginTop
        )
        // Check if click is within hitbox
        return if (hitBox.contains(x, y)) this else null
    }
}