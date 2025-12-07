package org.roldy.pawn

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import org.roldy.core.*
import org.roldy.core.pathwalker.PathWalker
import org.roldy.core.pathwalker.PathWalkerManager
import org.roldy.core.renderer.Layered

class PawnFigure(
    val batch: SpriteBatch,
) : Disposable, Renderable, Placeable, PathWalker, TiledObject {
    val pathWalkerManager = PathWalkerManager(this)
    val tex = Texture("purple_circle.png")
    val sprite = Sprite(tex).apply {
        setSize(100f, 100f)
        setOriginCenter()
    }


    context(deltaTime: Float)
    override fun render() {
        sprite.draw(batch)
        pathWalkerManager.walk()
    }


    override fun dispose() {
        tex.dispose()
        batch.dispose()
    }

    override val layer: Int
        get() = Layered.LAYER_2

    override val zIndex: Float
        get() = sprite.y - sprite.height / 2


    var _position = Vector2()
    override var position: Vector2
        get() = _position.cpy()  // Return copy
        set(value) {
            _position.set(value)
            sprite.setCenter(value.x, value.y)
        }

    override fun pathWalking(path: List<PathWalker.PathNode>) {
        pathWalkerManager.currentPath = path
        logger.info { "Found path: $path" }
    }

    override var coords: Vector2Int
        get() = pathWalkerManager.coords
        set(value) {
            pathWalkerManager.coords = value
        }
}