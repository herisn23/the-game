package org.roldy.core.renderer.drawable

import com.badlogic.gdx.utils.Pool
import org.roldy.core.renderer.chunk.ChunkObjectData

class DrawablePool<T: ChunkObjectData>(
    private val instance: () -> Drawable<T>
) : Pool<Drawable<T>>() {
    override fun newObject(): Drawable<T> {
        return instance()
    }
    fun create() =
        instance()
}