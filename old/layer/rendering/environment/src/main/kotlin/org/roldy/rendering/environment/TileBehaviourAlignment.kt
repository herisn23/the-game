package org.roldy.rendering.environment

import com.badlogic.gdx.graphics.g2d.Sprite


fun Sprite.alignToCenter(width: Float, height: Float) {
    setPosition(x + width / 2, y + height / 2)
}