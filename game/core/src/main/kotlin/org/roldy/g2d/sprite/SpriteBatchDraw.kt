package org.roldy.g2d.sprite

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch


fun SpriteBatch.draw(draw: SpriteBatch.() -> Unit) {
    begin()
    draw()
    end()
}

operator fun SpriteBatch.invoke(draw: SpriteBatch.() -> Unit) = draw(draw)

infix fun SpriteBatch.draw(sprite: Sprite) {
    sprite.draw(this)
}
infix fun SpriteBatch.draw(sprite: Lazy<Sprite>) {
    sprite.value.draw(this)
}