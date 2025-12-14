package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.data.state.SettlementState
import org.roldy.rendering.environment.TileBehaviour
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.g2d.Layered

class SettlementTileObject : TileBehaviour {
    val spriteObject = SpriteTileObject()

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

    var data: Data? = null

    val borders: MutableList<Sprite> = mutableListOf()

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        borders.forEach { spriteObject ->
            spriteObject.draw(batch)
        }
        spriteObject.draw(batch)
    }

    override fun bind(data: TileObject.Data) {
        spriteObject.bind(data)
        this.data = data as Data
        data.run {
            settlementData.region.map {
                Sprite(borderTextureRegion).apply {
                    val position = worldPosition(it)
                    color = settlementData.ruler.color
                    setSize(borderTextureRegion.regionWidth.toFloat(), borderTextureRegion.regionHeight.toFloat())
                    setOriginCenter()
                    setPosition(position.x, position.y)
                    setRotation(data.rotation)
                    setAlpha(0.3f)
                }
            }.let(borders::addAll)

        }
    }

    override val zIndex: Float get() = spriteObject.zIndex
    override val layer: Int get() = spriteObject.layer

    override fun reset() {
        spriteObject.reset()
        borders.clear()
    }
}