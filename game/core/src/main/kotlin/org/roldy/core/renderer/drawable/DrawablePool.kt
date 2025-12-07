package org.roldy.core.renderer.drawable

import com.badlogic.gdx.utils.Pool
import org.roldy.core.renderer.chunk.ChunkObjectData

class DrawablePool<T: ChunkObjectData>(
    private val instance: () -> ChunkManagedDrawable<T>
) : Pool<ChunkManagedDrawable<T>>() {
    override fun newObject(): ChunkManagedDrawable<T> {
        return instance()
    }
    fun create() =
        instance()
}