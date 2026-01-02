package org.roldy.core.utils

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Align

fun Sprite.setPosition(x: Float, y: Float, align: Int) {
    when (align) {
        Align.center -> setCenter(x, y)
        Align.bottom -> setPosition(x - width / 2f, y)
        Align.top -> setPosition(x - width / 2f, y - height)
        Align.left -> setPosition(x, y - height / 2f)
        Align.right -> setPosition(x - width, y - height / 2f)
        Align.bottomLeft -> setPosition(x, y)
        Align.bottomRight -> setPosition(x - width, y)
        Align.topLeft -> setPosition(x, y - height)
        Align.topRight -> setPosition(x - width, y - height)
        else -> setPosition(x, y)  // Default to bottom-left
    }
}