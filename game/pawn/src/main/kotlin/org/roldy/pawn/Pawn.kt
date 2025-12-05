package org.roldy.pawn

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.Renderable
import org.roldy.pawn.skeleton.PawnSkeletonManager
import org.roldy.pawn.skeleton.attribute.Slash1H

class Pawn(
    val camera: Camera,
    val batch: SpriteBatch
) : Renderable {
    val speed: Float = 10f

    val pawnManager: PawnSkeletonManager by lazy {
        PawnSkeletonManager(batch).apply {
            addEventListener(Slash1H) { _, _ ->
                println("hit")
            }
        }
    }
    override val zIndex: Float
        get() = pawnManager.zIndex

    context(delta: Float)
    override fun render() {
        camera.position.set(pawnManager.x, pawnManager.y, 0f)
        camera.update()
        context(delta, this) {
            pawnManager.render()
        }
    }
}