package org.roldy.pawn.skeleton

import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.AnimationStateData
import org.roldy.animation.AnimationTypeEventListenerHandler
import org.roldy.animation.add
import org.roldy.animation.listener
import org.roldy.pawn.skeleton.attribute.Idle
import org.roldy.pawn.skeleton.attribute.Slash1H

class PawnAnimator(
    private val stateData: AnimationStateData,
    internal val pawn: PawnSkeleton
) : AnimationTypeEventListenerHandler<PawnAnimator>(), PawnAnimation {
    val state = AnimationState(stateData)

    init {
        //init animation blending
        stateData.setMix(Idle.name, Slash1H.name, 0.1f)
        stateData.setMix(Slash1H.name, Idle.name, 0.1f)
        stateData.defaultMix = 0.2f

        state add listener(
            complete = {
                if (animation.name == Slash1H.name) {
                    idle()
                }
            },
            event = { ev ->
                propagate(this@PawnAnimator, ev)
            }
        )
    }

    override fun idle() {
        state.setAnimation(0, Idle.name, true)
    }

    override fun slash1H() {
        val entry = state.setAnimation(0, Slash1H.name, false)
        entry.timeScale = 2f
    }

    fun update(deltaTime: Float) {
        state.update(deltaTime)
        state.apply(pawn.skeleton)
    }
}