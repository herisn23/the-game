package org.roldy.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.renderer.Layered

interface MapBehaviourObject: Layered {
    context(delta: Float, camera: Camera)
    fun draw(batch: SpriteBatch)
}