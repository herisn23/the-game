package org.roldy.core.stream.drawable

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.stream.Streamable
import org.roldy.core.stream.chunk.ChunkItem

interface Drawable: Streamable, ChunkItem {
    context(delta:Float)
    fun draw(batch: SpriteBatch)
}