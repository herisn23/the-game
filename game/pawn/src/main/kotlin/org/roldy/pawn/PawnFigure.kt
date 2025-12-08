package org.roldy.pawn

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Placeable
import org.roldy.core.Renderable
import org.roldy.core.TiledObject
import org.roldy.core.Vector2Int
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.pathwalker.PathWalker
import org.roldy.core.pathwalker.PathWalkerManager
import org.roldy.core.renderer.Layered

class PawnFigure(
    val batch: SpriteBatch,
) : AutoDisposableAdapter(), Renderable, Placeable, PathWalker, TiledObject {
    val pathWalkerManager = PathWalkerManager(this)
    val tex = Texture("purple_circle.png").disposable()
    val sprite = Sprite(tex).apply {
        setSize(100f, 100f)
        setOriginCenter()
    }


    context(deltaTime: Float)
    override fun render() {
        sprite.draw(batch)
        pathWalkerManager.walk()
    }

    override val layer: Int
        get() = Layered.LAYER_3

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
        pathWalkerManager.path = path
    }

    override var coords: Vector2Int
        get() = pathWalkerManager.coords
        set(value) {
            pathWalkerManager.coords = value
        }
}