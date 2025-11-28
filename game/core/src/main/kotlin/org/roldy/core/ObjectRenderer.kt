package org.roldy.core

import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface ObjectRenderer {
    context(deltaTime: Float, batch: SpriteBatch)
    fun render()
    fun dispose() {}
}