package org.roldy.rendering.pawn

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.roldy.core.TilePositioned
import org.roldy.core.Vector2Int
import org.roldy.core.WorldPositioned
import org.roldy.core.pathwalker.PathWalker
import org.roldy.core.pathwalker.PathWalkerManager
import org.roldy.core.pathwalker.TileWalker
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.Renderable
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable

class PawnFigure(
    val walker: TileWalker,
    val walkCost: (Vector2Int) -> Float,
) : AutoDisposableAdapter(), Renderable, WorldPositioned, PathWalker, TilePositioned {
    val pathWalkerManager = PathWalkerManager(this, { walkCost(it) }, walker)
    val tex by disposable { Texture("purple_circle.png") }

    val sprite = Sprite(tex).apply {
        setSize(100f, 100f)
        setOriginCenter()
    }

    override var walkable: Boolean = false


    context(deltaTime: Float)
    override fun render(batch: SpriteBatch) {
        sprite.draw(batch)
        pathWalkerManager.walk()
    }

    override val layer: Int
        get() = Layered.LAYER_3

    override val zIndex: Float
        get() = sprite.y - sprite.height / 2


    var positionTmp = Vector2()
    override var position: Vector2
        get() = positionTmp.cpy()  // Return copy
        set(value) {
            positionTmp.set(value)
            sprite.setCenter(value.x, value.y)
        }

    override fun pathWalking(path: PathWalker.Path) {
        pathWalkerManager.path = path.tiles
    }

    override var coords: Vector2Int
        get() = walker.coords
        set(value) {
            walker.coords = value
        }
}