package org.roldy.rendering.environment.composite

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

data class CompositeTexture(
    val position: Vector2,
    val region: TextureRegion
)