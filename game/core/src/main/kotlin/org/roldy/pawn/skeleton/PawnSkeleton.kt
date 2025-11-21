package org.roldy.pawn.skeleton

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.spine.*
import org.roldy.ObjectRenderer
import org.roldy.asset.loadAsset
import org.roldy.g2d.sprite.invoke
import org.roldy.pawn.skeleton.attribute.*

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
    private val skinSlots: Map<PawnSkeletonPart, Slot> = skeleton.run {
        SkinPawnSkeletonPart.allParts.associateWith { findSlot(it.value) }
    }
    private val customizableSlots = skeleton.run {
        CustomizablePawnSkinPart.allParts.associateWith { findSlot(it.value) }
    }

    private val skeletonRenderer: SkeletonRenderer = SkeletonRenderer()
    private val stateData: AnimationStateData = AnimationStateData(skeletonData)
    private val animationState: AnimationState = AnimationState(stateData)

    init {
        skeleton.setPosition(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2)
        skinSlots.forEach {
            it.value.data.color.set(defaultColor)
        }
        skeleton.setToSetupPose()
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        skeletonRenderer.setPremultipliedAlpha(true)
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