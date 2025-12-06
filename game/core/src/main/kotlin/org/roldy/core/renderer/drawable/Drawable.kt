package org.roldy.core.renderer.drawable

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.renderer.Layered
import org.roldy.core.renderer.chunk.ChunkItem
import org.roldy.core.renderer.chunk.ChunkObjectData

interface Drawable<D:ChunkObjectData>: Layered, ChunkItem<D> {
    context(delta:Float)
    fun draw(batch: SpriteBatch)
}