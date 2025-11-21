package org.roldy.pawn.renderer

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.ObjectRenderer
import org.roldy.pawn.skeleton.PawnSkeleton
import org.roldy.pawn.skeleton.attribute.Front

class PawnRenderer : ObjectRenderer {
    val skeletons = listOf(
        PawnSkeleton(Front)
    ).associateBy(PawnSkeleton::orientation)
    var currentOrientation = Front


    override fun render(deltaTime: Float, batch: SpriteBatch) {
        skeletons[currentOrientation]?.render(deltaTime, batch)
    }

}