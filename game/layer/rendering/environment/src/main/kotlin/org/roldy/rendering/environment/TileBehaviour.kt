package org.roldy.rendering.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import org.roldy.rendering.g2d.Layered

interface TileBehaviour : Layered, Pool.Poolable {

    context(delta: Float, camera: Camera)
    fun draw(batch: SpriteBatch)
    fun bind(data: TileObject.Data)
}