package org.roldy.environment

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.renderer.drawable.Drawable

class MapObject : Drawable<MapObjectData> {

    lateinit var behaviour: MapBehaviourObject

    override val layer: Int get() = behaviour.layer
    override val zIndex: Float get() = behaviour.zIndex

    context(delta: Float)
    override fun draw(batch: SpriteBatch) {
        behaviour.draw(batch)
    }

    override fun bind(data: MapObjectData) {
        behaviour = data.createBehaviour(data)
    }
}