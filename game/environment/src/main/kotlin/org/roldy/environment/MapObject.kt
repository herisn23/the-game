package org.roldy.environment

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.renderer.Sortable
import org.roldy.core.renderer.chunk.ChunkObjectData
import org.roldy.core.renderer.drawable.Drawable

class MapObject : Drawable {

    override var layer: Int = 0
    /**
     * Create empty sprite
     * This object is managed by pool and world streamer
     * Sprite data are provided by bind function
     * */

    var sprite: Sprite = Sprite()

    context(delta: Float)
    override fun draw(batch: SpriteBatch) {
        sprite.draw(batch)
    }

    override val zIndex: Float get() = sprite.run { y - height / 2f }

    override fun bind(data: ChunkObjectData) {
        if(data.isRoad) {
            data.bindRoad()
        } else
            data.bindSettlement()

    }

    private fun ChunkObjectData.bindRoad() {
        atlas.findRegion("road")?.let { region->
            layer =  Sortable.LAYER_1
            sprite.run {
                val s = 200f
                setSize(s, s)
                setRegion(region)
                setCenter(position.x, position.y)
                setOriginCenter()
                setScale(1f)
                setRotation(0f)
            }
        }
    }

    private fun ChunkObjectData.bindSettlement() {
        atlas.findRegion("house")?.let { region->
            layer =  Sortable.LAYER_2
            sprite.run {
//                val s = 100f
                setSize(region.regionWidth.toFloat(), region.regionHeight.toFloat())
                setRegion(region)
                setCenter(position.x, position.y)
                setOriginCenter()
                setScale(1f)
                setRotation(0f)
            }
        }
    }
}