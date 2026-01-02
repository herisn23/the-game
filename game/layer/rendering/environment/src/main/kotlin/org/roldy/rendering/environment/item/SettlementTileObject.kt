package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.data.state.SettlementState
import org.roldy.rendering.environment.SpriteTileBehaviour
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.Layered

class SettlementTileObject : SpriteTileBehaviour<SettlementTileObject.Data>() {

    data class Data(
        override val position: Vector2,
        override val coords: Vector2Int,
        override val textureRegion: TextureRegion,
        override val rotation: Float = 0f,
        override val layer: Int = Layered.LAYER_2,
        override val data: Map<String, Any> = emptyMap(),
        val borderTextureRegion: TextureRegion,
        val settlementData: SettlementState,
        val worldPosition: (Vector2Int) -> Vector2,
        val inBounds: (Vector2Int) -> Boolean
    ) : SpriteTileObject.ISpriteData

    val borders: MutableList<Sprite> = mutableListOf()

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        borders.forEach { spriteObject ->
            spriteObject.draw(batch)
        }
        super.draw(batch)
    }

    override fun bind(data: TileObject.Data) {
        super.bind(data)
        this.data?.run {
            settlementData.region.map {
                Sprite(borderTextureRegion).apply {
                    val position = worldPosition(it)
                    color = settlementData.ruler.color
                    setSize(borderTextureRegion.regionWidth.toFloat(), borderTextureRegion.regionHeight.toFloat())
                    setOriginCenter()
                    setPosition(position.x, position.y)
                    setRotation(rotation)
                    setAlpha(0.1f)
                }
            }.let(borders::addAll)

        }
    }


    override fun reset() {
        super.reset()
        borders.clear()
    }
}