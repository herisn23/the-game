package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.core.x
import org.roldy.rendering.environment.TileBehaviourAdapter
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SpriteTileBehaviour.ISpriteData
import org.roldy.rendering.environment.zIndex
import org.roldy.rendering.g2d.Layered

class SpriteTileBehaviour : TileBehaviourAdapter<ISpriteData>() {

    class Data(
        override val position: Vector2,
        override val offset: Vector2 = 0f x 0f,
        override val coords: Vector2Int,
        override val data: Map<String, Any> = emptyMap(),
        override val textureRegion: TextureRegion,
        override val rotation: Float = 0f,
        override val scaleX: Float = 1f,
        override val scaleY: Float = 1f,
        override val layer: Int = Layered.LAYER_2
    ) : ISpriteData

    interface ISpriteData : TileObject.Data {
        val textureRegion: TextureRegion
        val rotation: Float
        val offset: Vector2
        val scaleX: Float
        val scaleY: Float
        val layer: Int
    }

    val sprite = Sprite()


    override val zIndex: Float get() = sprite.zIndex
    override val layer: Int get() = data?.layer ?: Layered.LAYER_2

    context(delta: Float, camera: Camera)
    override fun draw(data: ISpriteData, batch: SpriteBatch) {
        sprite.draw(batch)
    }

    override fun configure(data: ISpriteData) {
        sprite.setRegion(data.textureRegion)
        sprite.setScale(data.scaleX, data.scaleY)
        sprite.setSize(data.textureRegion.regionWidth.toFloat(), data.textureRegion.regionHeight.toFloat())
        sprite.setOriginCenter()
        sprite.setRotation(data.rotation)
        sprite.setPosition(data.position.x + data.offset.x, data.position.y + data.offset.y)
    }

    override fun reset() {
        super.reset()
        sprite.setScale(1f)
        sprite.rotation = 0f
    }
}