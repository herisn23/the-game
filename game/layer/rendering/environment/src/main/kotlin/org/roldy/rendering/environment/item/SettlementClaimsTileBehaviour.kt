package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.rendering.environment.TileBehaviourAdapter
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.Layered

class SettlementClaimsTileBehaviour : TileBehaviourAdapter<SettlementClaimsTileBehaviour.Data>() {

    override val zIndex: Float get() = data?.position?.y ?: 0f
    override val layer: Int get() = Layered.LAYER_0
    val sprite = Sprite()

    data class Data(
        override val data: Map<String, Any> = emptyMap(),
        val worldPosition: (Vector2Int) -> Vector2,
        val region: TextureRegion,
        val color: Color,
        override val coords: Vector2Int,
        override val position: Vector2
    ) : TileObject.Data

    context(delta: Float, camera: Camera)
    override fun draw(
        data: Data,
        batch: SpriteBatch
    ) {
        sprite.draw(batch)
    }

    override fun configure(data: Data) {
        sprite.apply {
            setRegion(data.region)
            color = data.color
            setSize(data.region.regionWidth.toFloat(), data.region.regionHeight.toFloat())
            setOriginCenter()
            setPosition(data.position.x, data.position.y)
            setRotation(rotation)
        }
    }


    override fun reset() {
        super.reset()
        sprite.texture = null
    }
}