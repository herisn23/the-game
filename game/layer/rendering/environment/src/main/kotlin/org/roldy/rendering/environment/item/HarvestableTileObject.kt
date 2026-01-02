package org.roldy.rendering.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.data.state.HarvestableState
import org.roldy.rendering.environment.SpriteTileBehaviour
import org.roldy.rendering.g2d.Layered

class HarvestableTileObject : SpriteTileBehaviour<HarvestableTileObject.Data>() {

    data class Data(
        override val position: Vector2,
        override val coords: Vector2Int,
        override val textureRegion: TextureRegion,
        override val rotation: Float = 0f,
        override val layer: Int = Layered.LAYER_2,
        override val data: Map<String, Any> = emptyMap(),
        val state: HarvestableState
    ) : SpriteTileObject.ISpriteData

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        data?.state?.refreshing?.supplies?.let {
            if (it > 0) {
                super.draw(batch)
            }
        }
    }
}