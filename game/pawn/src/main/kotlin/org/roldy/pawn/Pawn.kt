package org.roldy.pawn

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.roldy.core.Placeable
import org.roldy.core.Renderable
import org.roldy.core.x
import org.roldy.pawn.handler.MoveHandler
import org.roldy.pawn.skeleton.PawnSkeletonManager
import org.roldy.pawn.skeleton.attribute.Slash1H

class Pawn(
    val batch: SpriteBatch
) : Renderable, Placeable {
    val walkSpeed: Float = 1f
    private val move = MoveHandler(this)
    val manager: PawnSkeletonManager by lazy {
        PawnSkeletonManager(batch).apply {
            addEventListener(Slash1H) { _, _ ->
                println("hit")
            }
        }
    }
    override val layer: Int = manager.layer
    override val zIndex: Float
        get() = manager.zIndex

    context(delta: Float)
    override fun render() {
        context(delta, this) {
            manager.render()
        }
    }

    override var position: Vector2
        get() = manager.x x manager.y
        set(value) {
            manager.setPosition(value)
        }
}