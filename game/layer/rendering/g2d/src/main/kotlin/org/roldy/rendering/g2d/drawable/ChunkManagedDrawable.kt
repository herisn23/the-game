package org.roldy.rendering.g2d.drawable

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.rendering.g2d.chunk.ChunkItem
import org.roldy.rendering.g2d.chunk.ChunkObjectData

interface ChunkManagedDrawable<D : ChunkObjectData> : ChunkItem<D> {
    context(delta: Float, camera: Camera)
    fun draw(batch: SpriteBatch)
}