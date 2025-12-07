package org.roldy.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import org.roldy.core.renderer.Layered

interface MapBehaviourObject: Layered, Disposable {
    context(delta: Float, camera: Camera)
    fun draw(batch: SpriteBatch)
}