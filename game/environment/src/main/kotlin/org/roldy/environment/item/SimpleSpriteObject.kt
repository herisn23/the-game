package org.roldy.environment.item

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.renderer.Layered
import org.roldy.environment.MapBehaviourObject
import org.roldy.environment.MapObjectData

abstract class SimpleSpriteObject(
    data: MapObjectData,
    region: TextureRegion,
    initializeSprite: Sprite.() -> Unit = {}
) : MapBehaviourObject {

    val sprite = Sprite(region).apply {
        setCenter(data.position.x, data.position.y)
        initializeSprite()
    }

    override val zIndex: Float get() = sprite.run { y - height / 2f }
    override val layer: Int = Layered.LAYER_2

    context(delta: Float)
    override fun draw(batch: SpriteBatch) {
        sprite.draw(batch)
    }
}