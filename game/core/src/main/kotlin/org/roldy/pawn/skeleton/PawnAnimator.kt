package org.roldy.pawn.skeleton

import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Skeleton
import org.roldy.pawn.skeleton.attribute.AttackRightHand
import org.roldy.pawn.skeleton.attribute.Idle

class PawnAnimator(
    private val state: AnimationState,
    private val skeleton: Skeleton
) : PawnAnimation {


    override fun idle() {
        state.setAnimation(0, Idle.name, true)
    }

    override fun attackRightHand() {
        state.setAnimation(0, AttackRightHand.name, true)
    }

    fun update(deltaTime: Float) {
        state.update(deltaTime)
        state.apply(skeleton)
    }
}