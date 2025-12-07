package org.roldy.core.renderer.drawable

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import org.roldy.core.renderer.Layered
import org.roldy.core.renderer.chunk.ChunkItem
import org.roldy.core.renderer.chunk.ChunkObjectData

interface Drawable<D:ChunkObjectData>: Layered, ChunkItem<D>, Disposable {
    context(delta: Float, camera: Camera)
    fun draw(batch: SpriteBatch)
}