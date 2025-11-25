package org.roldy.pawn.skeleton

import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.AnimationStateData
import org.roldy.animation.AnimationTypeEventListenerHandler
import org.roldy.animation.add
import org.roldy.animation.listener
import org.roldy.pawn.skeleton.attribute.*

class PawnAnimator(
    private val stateData: AnimationStateData,
    internal val pawn: PawnSkeleton
) : AnimationTypeEventListenerHandler<PawnAnimator>(), PawnAnimation {

    val idleTrack = 0        // Base idle (full body)
    val walkLTrack = 1       // Legs layer
    val walkUTrack = 2       // Upper body layer
    val slashTrack = 3       // Slash/attack layer (highest priority)

    val state = AnimationState(stateData)
    init {
        //init animation blending
        stateData.setMix(Idle.name, WalkU.name, 0.2f);
        stateData.setMix(WalkU.name, Idle.name, 0.2f);
        stateData.setMix(Idle.name, Slash1H.name, 0.1f);
        stateData.setMix(Slash1H.name, Idle.name, 0.2f);
        stateData.setMix(WalkU.name, Slash1H.name, 0.1f);
        stateData.setMix(Slash1H.name, WalkU.name, 0.2f);
        stateData.setMix(WalkL.name, Idle.name, 0.2f);
        stateData.setMix(Idle.name, WalkL.name, 0.2f);
        stateData.defaultMix = 0.2f

        state add listener(
            complete = {
                if (animation.name == Slash1H.name) {
                    state.clearTrack(slashTrack)
                }
            },
            event = { ev ->
                propagate(this@PawnAnimator, ev)
            }
        )
    }

    override fun idle() {
        state.clearTrack(walkLTrack)
        state.clearTrack(walkUTrack)
        state.setAnimation(idleTrack, Idle.name, true)
    }

    override fun slash1H() {
        state.setAnimation(slashTrack, Slash1H.name, false).apply {
            timeScale = 2f
        }
    }

    override fun walk() {
        // Clear idle since we're moving
        state.clearTrack(idleTrack)
        // Set both leg and upper body walk animations
        state.setAnimation(walkLTrack, WalkL.name, true).apply {
            timeScale = 2f
        }
        state.setAnimation(walkUTrack, WalkU.name, true).apply {
            timeScale = 2f
        }
    }

    override fun stop() {
        // Clear walk tracks
        state.clearTrack(walkLTrack)
        state.clearTrack(walkUTrack)

        // Reset only leg bones to setup pose
        pawn.skeleton.findBone(PawnBodySkeletonSlot.LegLeft.name)?.setToSetupPose()  // Use your actual bone names
        pawn.skeleton.findBone(PawnBodySkeletonSlot.LegRight.name)?.setToSetupPose()
        // Switch to idle animation
       idle()
    }

    fun update(deltaTime: Float) {
        state.update(deltaTime)
        state.apply(pawn.skeleton)
    }
}