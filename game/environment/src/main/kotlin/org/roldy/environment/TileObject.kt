package org.roldy.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import org.roldy.core.Vector2Int
import org.roldy.core.renderer.chunk.ChunkObjectData
import org.roldy.core.renderer.drawable.ChunkManagedDrawable


class TileObject : ChunkManagedDrawable<TileObject.Data>, Pool.Poolable {

    interface Data: ChunkObjectData {
        val coords: Vector2Int
    }

    var behaviour: TileBehaviour? = null

    override val layer: Int get() = behaviour?.layer ?: 0
    override val zIndex: Float get() = behaviour?.zIndex ?: 0f

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        behaviour?.draw(batch)
    }

    override fun bind(data: Data) {
        behaviour = BehaviourPool.obtain(data).apply {
            bind(data)
        }
    }

    override fun reset() {
        behaviour?.reset()
    }
}