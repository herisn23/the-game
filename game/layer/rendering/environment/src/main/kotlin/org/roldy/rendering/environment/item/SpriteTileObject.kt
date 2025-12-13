package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.rendering.environment.TileBehaviour
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.Layered

class SpriteTileObject : TileBehaviour {

    class Data(
        override val name: String,
        override val position: Vector2,
        override val coords: Vector2Int,
        override val data: Map<String, Any> = emptyMap(),
        override val textureRegion: TextureRegion,
        override val rotation: Float = 0f,
        override val layer: Int = Layered.LAYER_2
    ) : ISpriteData

    interface ISpriteData : TileObject.Data {
        val textureRegion: TextureRegion
        val rotation: Float
        val layer: Int
    }

    val sprite = Sprite()
    var data: ISpriteData? = null

    override val zIndex: Float get() = sprite.run { y - height / 2f }
    override val layer: Int get() = data?.layer ?: Layered.LAYER_2

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        sprite.draw(batch)
    }

    override fun bind(data: TileObject.Data) {
        this.data = data as ISpriteData

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