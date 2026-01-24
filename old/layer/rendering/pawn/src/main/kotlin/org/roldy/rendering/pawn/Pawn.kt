package org.roldy.rendering.pawn

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.roldy.core.WorldPositioned
import org.roldy.core.x
import org.roldy.rendering.g2d.Renderable
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.pawn.skeleton.PawnSkeletonManager
import org.roldy.rendering.pawn.skeleton.attribute.Slash1H

class Pawn : AutoDisposableAdapter(), Renderable, WorldPositioned {
    val manager: PawnSkeletonManager by disposable {
        PawnSkeletonManager().apply {
            addEventListener(Slash1H) { _, _ ->
                println("hit")
            }
        }
    }
    override val layer: Int = manager.layer
    override val zIndex: Float
        get() = manager.zIndex

    context(delta: Float)
    override fun render(batch: SpriteBatch) {
        context(delta) {
            manager.render(batch)
        }
    }

    override var position: Vector2
        get() = manager.x x manager.y
        set(value) {
            manager.setPosition(value)
        }
}