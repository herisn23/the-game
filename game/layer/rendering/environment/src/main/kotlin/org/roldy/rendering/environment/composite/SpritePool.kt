package org.roldy.rendering.environment.composite

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool

class SpritePool : Pool<Sprite>() {
    override fun newObject(): Sprite = Sprite()

    override fun reset(sprite: Sprite) {
        sprite.setScale(1f)
        sprite.rotation = 0f
    }
}