package org.roldy.pawn.skeleton

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.spine.*
import org.roldy.ObjectRenderer
import org.roldy.asset.loadAsset
import org.roldy.g2d.sprite.invoke
import org.roldy.pawn.Pawn
import org.roldy.pawn.skeleton.attribute.ArmLeft
import org.roldy.pawn.skeleton.attribute.ArmRight
import org.roldy.pawn.skeleton.attribute.Body
import org.roldy.pawn.skeleton.attribute.HandLeft
import org.roldy.pawn.skeleton.attribute.HandRight
import org.roldy.pawn.skeleton.attribute.Head
import org.roldy.pawn.skeleton.attribute.Idle
import org.roldy.pawn.skeleton.attribute.LegLeft
import org.roldy.pawn.skeleton.attribute.LegRight
import org.roldy.pawn.skeleton.attribute.PawnSkeletonOrientation
import org.roldy.pawn.skeleton.attribute.PawnSkeletonPart

class PawnSkeleton(
    val orientation: PawnSkeletonOrientation
) : ObjectRenderer {
    private val defaultColor: Color = Color.valueOf("FFC878")
    private val atlas: TextureAtlas = TextureAtlas(loadAsset("skeleton/human/${orientation.value}/skeleton.atlas"))
    private val binary: SkeletonBinary = SkeletonBinary(atlas)
    private val skeletonData: SkeletonData = binary.readSkeletonData(
        loadAsset("skeleton/human/${orientation.value}/skeleton.skel")
    )
    private val skeleton: Skeleton = Skeleton(skeletonData)
    private val slots: Map<PawnSkeletonPart, Slot> = skeleton.run {
        mapOf(
            Head to findSlot(Head.value),
            Body to findSlot(Body.value),
            ArmLeft to findSlot(ArmLeft.value),
            ArmRight to findSlot(ArmRight.value),
            HandLeft to findSlot(HandLeft.value),
            HandRight to findSlot(HandRight.value),
            LegLeft to findSlot(LegLeft.value),
            LegRight to findSlot(LegRight.value)
        )
    }

    private val skeletonRenderer: SkeletonRenderer = SkeletonRenderer()
    private val stateData: AnimationStateData = AnimationStateData(skeletonData)
    private val animationState: AnimationState = AnimationState(stateData)

    init {
        skeleton.setPosition(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2)
        slots.forEach {
            it.value.data.color.set(defaultColor)
        }
        skeleton.setToSetupPose()
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        animationState.setAnimation(0, Idle.value, true)
    }

    override fun render(deltaTime: Float, batch: SpriteBatch) {
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        animationState.update(deltaTime)
        animationState.apply(skeleton)
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        skeleton.update(deltaTime)
        batch {
            skeletonRenderer.draw(this, skeleton)
        }
    }
}