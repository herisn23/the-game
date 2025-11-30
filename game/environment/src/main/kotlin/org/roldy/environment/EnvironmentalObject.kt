package org.roldy.environment

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.asset.loadAsset
import org.roldy.core.renderer.LayeredObject
import org.roldy.utils.invoke

class EnvironmentalObject(
    private val camera: OrthographicCamera
) : LayeredObject {
    val tex = Texture(loadAsset("environment/tile241.png")).let(::TextureRegion)
    val sprite = Sprite(tex).apply {
        setCenter(0f, 0f)
        setSize(500f, 500f)
    }
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