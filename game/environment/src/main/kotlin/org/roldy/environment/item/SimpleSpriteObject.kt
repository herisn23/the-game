package org.roldy.environment.item

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.roldy.core.renderer.Layered
import org.roldy.environment.MapBehaviourObject
import org.roldy.environment.MapObjectData

abstract class SimpleSpriteObject(
    data: MapObjectData,
    region: TextureRegion,
    initializeSprite: Sprite.() -> Unit = {}
) : MapBehaviourObject {

    val sprite = Sprite(region).apply {
        setPosition(data.position.x, data.position.y)
        initializeSprite()
    }

    override val zIndex: Float get() = sprite.run { y - height / 2f }
    override val layer: Int = Layered.LAYER_2

    context(delta: Float, camera: Camera)
    override fun draw(batch: SpriteBatch) {
        sprite.draw(batch)
//        renderBoundaries()
    }

    val shape = ShapeRenderer()

    context(delta: Float, camera: Camera)
    private fun renderBoundaries() {
        shape.projectionMatrix = camera.combined
        shape.begin(ShapeRenderer.ShapeType.Line)
        shape.color = Color.WHITE
        shape.rect(sprite.x, sprite.y, sprite.width, sprite.height)
        shape.end()
    }

    override fun dispose() {}
}