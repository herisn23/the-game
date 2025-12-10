package org.roldy.rendering.g2d.drawable

import com.badlogic.gdx.utils.Pool
import org.roldy.rendering.g2d.chunk.ChunkObjectData

class DrawablePool<T: ChunkObjectData>(
    private val instance: () -> ChunkManagedDrawable<T>
) : Pool<ChunkManagedDrawable<T>>() {
    override fun newObject(): ChunkManagedDrawable<T> {
        return instance()
    }
}