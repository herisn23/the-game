package org.roldy.pawn.handler

import com.badlogic.gdx.math.Vector2
import org.roldy.pawn.Pawn
import org.roldy.pawn.skeleton.attribute.*
import kotlin.math.abs

class MoveHandler(
    val pawn: Pawn
) {

    fun towards(position: Vector2) {
        val orientation = pawn.position.getDirectionTo(position)
        pawn.manager.currentOrientation = orientation
        pawn.manager.walk(pawn.walkSpeed, position)
    }

    // Extension function
    fun Vector2.getDirectionTo(target: Vector2):PawnSkeletonOrientation  {
        val dx = target.x - this.x
        val dy = target.y - this.y

        return if (abs(dx) > abs(dy)) {
            // Horizontal movement is dominant
            if (dx > 0) Right else Left
        } else {
            // Vertical movement is dominant
            if (dy > 0) Back else Front
        }
    }

}