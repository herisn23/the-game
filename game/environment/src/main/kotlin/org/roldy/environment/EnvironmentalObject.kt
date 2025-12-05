package org.roldy.environment

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.renderer.chunk.ChunkObjectData
import org.roldy.core.renderer.drawable.Drawable

class EnvironmentalObject(
    val atlas: TextureAtlas,
) : Drawable {
    /**
     * Create empty sprite
     * This object is managed by pool and world streamer
     * Sprite data are provided by bind function
     * */

    var sprite: Sprite? = null

    context(delta: Float)
    override fun draw(batch: SpriteBatch) {
        sprite?.draw(batch)
    }

    override val zIndex: Float get() = sprite?.run { y - height / 2f } ?: 0f

    override fun bind(data: ChunkObjectData) {
        atlas.findRegion(data.region)?.let { region->
            if (sprite == null) {
                sprite = Sprite(region)
            }
            sprite?.run {
                val s = 100f
                setSize(s, s)
                setRegion(region)
                setCenter(data.x, data.y)
                setOriginCenter()
                setScale(1f)
                setRotation(0f)
            }
        }
    }
}