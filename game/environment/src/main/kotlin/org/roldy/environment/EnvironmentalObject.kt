package org.roldy.environment

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import org.roldy.utils.invoke

class EnvironmentalObject(
    private val camera: OrthographicCamera,
    private val tex: Texture,
    x: Float,
    y: Float,
) : LayeredObject {
    val sprite = Sprite(tex).apply {
        setCenter(x, y)
        setSize(500f, 500f)
    }

    private val boundingBox: Rectangle = Rectangle(
        pivotX,
        pivotY,
        sprite.width,
        sprite.height
    )

    override fun shouldRender(viewBounds: Rectangle): Boolean =
        viewBounds.overlaps(boundingBox)

    val batch = SpriteBatch()
    override val pivotX: Float
        get() = sprite.x// - sprite.width / 2f
    override val pivotY: Float
        get() = sprite.y - sprite.height / 2f

    context(deltaTime: Float)
    override fun render() {
        batch.projectionMatrix = camera.combined
        batch {
            sprite.draw(this)
        }
    }
}