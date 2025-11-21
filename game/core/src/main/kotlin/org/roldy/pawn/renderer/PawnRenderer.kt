package org.roldy.pawn.renderer

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.ObjectRenderer
import org.roldy.pawn.skeleton.PawnSkeleton
import org.roldy.pawn.skeleton.attribute.Front
import org.roldy.pawn.skeleton.attribute.PawnSkeletonOrientation

class PawnRenderer : ObjectRenderer {
    val skeletons: Map<PawnSkeletonOrientation, PawnSkeleton> = listOf(
        PawnSkeleton(Front)
    ).associateBy(PawnSkeleton::orientation)
    var currentOrientation: Front = Front

    context(deltaTime: Float, batch: SpriteBatch)
    override fun render() {
        skeletons[currentOrientation]?.render()
    }

}