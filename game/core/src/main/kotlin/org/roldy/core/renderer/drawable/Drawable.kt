package org.roldy.core.renderer.drawable

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.renderer.Sortable
import org.roldy.core.renderer.chunk.ChunkItem

interface Drawable: Sortable, ChunkItem {
    context(delta:Float)
    fun draw(batch: SpriteBatch)
}