package org.roldy.rendering.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.rendering.environment.item.SpriteTileBehaviour

abstract class SpriteTileBehaviourAdapter<Data : SpriteTileBehaviour.ISpriteData> : TileBehaviour {
    val spriteObject = SpriteTileBehaviour()
    var data: Data? = null


    override val zIndex: Float get() = spriteObject.zIndex
    override val layer: Int get() = spriteObject.layer

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        spriteObject.draw(batch)
    }


    override fun bind(data: TileObject.Data) {
        spriteObject.bind(data)
        this.data = data as? Data
    }

    override fun reset() {
        spriteObject.reset()
    }
}