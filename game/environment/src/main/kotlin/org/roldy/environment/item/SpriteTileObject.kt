package org.roldy.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.core.renderer.Layered
import org.roldy.environment.TileBehaviour
import org.roldy.environment.TileObject

class SpriteTileObject : TileBehaviour {

    class Data(
        override val name: String,
        override val position: Vector2,
        override val coords: Vector2Int,
        val textureRegion: TextureRegion,
        val rotation: Float = 0f,
        val layer: Int = Layered.LAYER_2
    ) : TileObject.Data

    val sprite = Sprite()
    var data: Data? = null

    override val zIndex: Float get() = sprite.run { y - height / 2f }
    override val layer: Int get() = data?.layer ?: Layered.LAYER_2

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        sprite.draw(batch)
    }

    override fun bind(data: TileObject.Data) {
        if (data !is Data) return
        this.data = data
        sprite.setRegion(data.textureRegion)
        sprite.setSize(data.textureRegion.regionWidth.toFloat(), data.textureRegion.regionHeight.toFloat())
        sprite.setOriginCenter()
        sprite.setPosition(data.position.x, data.position.y)
        sprite.setRotation(data.rotation)
    }

    override fun reset() {
        sprite.setScale(1f)
        sprite.rotation = 0f
    }
}