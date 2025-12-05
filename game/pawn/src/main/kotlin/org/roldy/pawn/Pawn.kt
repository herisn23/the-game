package org.roldy.pawn

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.Renderable
import org.roldy.pawn.skeleton.PawnSkeletonManager
import org.roldy.pawn.skeleton.attribute.Slash1H

class Pawn(
    val batch: SpriteBatch
) : Renderable {
    val walkSpeed: Float = 1f
    val runSpeed: Float = 100f

    val manager: PawnSkeletonManager by lazy {
        PawnSkeletonManager(batch).apply {
            addEventListener(Slash1H) { _, _ ->
                println("hit")
            }
        }
    }
    override val zIndex: Float
        get() = manager.zIndex

    context(delta: Float)
    override fun render() {
        context(delta, this) {
            manager.render()
        }
    }
}