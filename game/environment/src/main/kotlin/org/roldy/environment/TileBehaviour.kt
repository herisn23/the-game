package org.roldy.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import org.roldy.core.renderer.Layered

interface TileBehaviour : Layered, Pool.Poolable {

    context(delta: Float, camera: Camera)
    fun draw(batch: SpriteBatch)
    fun bind(data: TileObject.Data)
}