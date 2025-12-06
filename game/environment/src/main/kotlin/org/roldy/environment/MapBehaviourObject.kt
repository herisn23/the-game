package org.roldy.environment

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.renderer.Layered

interface MapBehaviourObject: Layered {
    context(delta: Float)
    fun draw(batch: SpriteBatch)
}