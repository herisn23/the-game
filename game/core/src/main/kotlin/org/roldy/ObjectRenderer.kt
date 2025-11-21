package org.roldy

import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface ObjectRenderer {
    fun render(deltaTime: Float, batch: SpriteBatch)
    fun dispose() {}
}