package org.roldy.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.renderer.drawable.Drawable

class MapObject : Drawable<MapObjectData> {

    var behaviour: MapBehaviourObject? = null

    override val layer: Int get() = behaviour?.layer ?: 0
    override val zIndex: Float get() = behaviour?.zIndex ?: 0f

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        behaviour?.draw(batch)
    }

    override fun bind(data: MapObjectData) {
        //always dispose on rebind to avoid memory leaks
        dispose()
        behaviour = data.createBehaviour(data)
    }

    override fun dispose() {
        behaviour?.dispose()
    }
}