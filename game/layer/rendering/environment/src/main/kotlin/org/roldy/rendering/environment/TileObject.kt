package org.roldy.rendering.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import org.roldy.core.Vector2Int
import org.roldy.data.tile.TileData
import org.roldy.rendering.g2d.chunk.ChunkObjectData
import org.roldy.rendering.g2d.drawable.ChunkManagedDrawable


class TileObject : ChunkManagedDrawable<TileObject.Data>, Pool.Poolable {

    interface Data : ChunkObjectData {
        val coords: Vector2Int
        val data: Map<String, Any>
        val tileData: List<TileData> get() = data.values.filterIsInstance<TileData>()
    }

    var behaviour: TileBehaviour? = null

    override val layer: Int get() = behaviour?.layer ?: 0
    override val zIndex: Float get() = behaviour?.zIndex ?: 0f

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        behaviour?.draw(batch)
    }

    var currentData: Data? = null

    override var data: Data?
        get() = currentData
        set(value) {
            currentData = value
            value?.let { data ->
                behaviour?.let(BehaviourPool::free)
                behaviour = BehaviourPool.obtain(data).apply {
                    bind(data)
                }
            }
        }

    override fun reset() {
        behaviour?.reset()
        behaviour = null
        currentData = null
    }
}