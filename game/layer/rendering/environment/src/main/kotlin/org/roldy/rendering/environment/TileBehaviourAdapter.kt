package org.roldy.rendering.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.rendering.g2d.Layered

abstract class TileBehaviourAdapter<D : TileObject.Data> : TileBehaviour {

    var data: D? = null

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        data?.let { data ->
            draw(data, batch)
        }
    }

    override val zIndex: Float
        get() = data?.run { position.y } ?: 0f

    override val layer: Int
        get() = Layered.LAYER_2

    context(delta: Float, camera: Camera)
    abstract fun draw(data: D, batch: SpriteBatch)

    override fun bind(data: TileObject.Data) {
        this.data = data as D
        this.data?.let {
            configure(it)
        }
    }

    abstract fun configure(data: D)

    override fun reset() {
        this.data = null
    }
}