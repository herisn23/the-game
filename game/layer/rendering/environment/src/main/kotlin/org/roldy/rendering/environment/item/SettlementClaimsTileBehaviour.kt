package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.core.utils.alpha
import org.roldy.rendering.environment.TileBehaviourAdapter
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.Layered

class SettlementClaimsTileBehaviour : TileBehaviourAdapter<SettlementClaimsTileBehaviour.Data>() {

    override val zIndex: Float get() = data?.position?.y ?: 0f
    override val layer: Int get() = Layered.LAYER_0
    val border = Sprite()
    val background = Sprite()

    data class Data(
        override val data: Map<String, Any> = emptyMap(),
        val worldPosition: (Vector2Int) -> Vector2,
        val borderRegion: TextureRegion?,
        val backgroundRegion: TextureRegion,
        val color: Color,
        override val coords: Vector2Int,
        override val position: Vector2
    ) : TileObject.Data

    context(delta: Float, camera: Camera)
    override fun draw(
        data: Data,
        batch: SpriteBatch
    ) {
        background.draw(batch)
        border.texture?.let {
            border.draw(batch)
        }
    }

    override fun configure(data: Data) {

        fun Sprite.configure(region: TextureRegion, color: Color) = apply {
            setRegion(region)
            this.color = color
            setSize(region.regionWidth.toFloat(), region.regionHeight.toFloat())
            setOriginCenter()
            setPosition(data.position.x, data.position.y)
            setRotation(rotation)
        }

        data.borderRegion?.let { region ->
            border.configure(region, data.color)
        }
        background.apply {
            configure(data.backgroundRegion, data.color alpha 0.1f)
        }
    }


    override fun reset() {
        super.reset()
        border.texture = null
        background.texture = null
    }
}