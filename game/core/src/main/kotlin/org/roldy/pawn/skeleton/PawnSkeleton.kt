package org.roldy.pawn.skeleton

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.esotericsoftware.spine.*
import com.esotericsoftware.spine.attachments.Attachment
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.ObjectRenderer
import org.roldy.asset.loadAsset
import org.roldy.pawn.skeleton.attribute.*

class PawnSkeleton(
    val orientation: PawnSkeletonOrientation
) : ObjectRenderer {

    data class SlotData(
        val slot: Slot,
        private val originAttachment: RegionAttachment
    ) {
        var currentAttachment: Attachment = originAttachment
        fun update(attachment: RegionAttachment) {
            this.currentAttachment = attachment
            attachment.x = originAttachment.x
            attachment.y = originAttachment.y
            attachment.rotation = originAttachment.rotation
            attachment.scaleX = originAttachment.scaleX
            attachment.scaleY = originAttachment.scaleY
            attachment.height = originAttachment.height
            attachment.width = originAttachment.width
            attachment.color.set(originAttachment.color)
            attachment.updateRegion()
        }
    }

    private val defaultColor: Color = Color.valueOf("FFC878")
    private val atlas: TextureAtlas = TextureAtlas(loadAsset("skeleton/human/${orientation.value}/skeleton.atlas"))
    private val binary: SkeletonBinary = SkeletonBinary(atlas)
    private val skeletonData: SkeletonData = binary.readSkeletonData(
        loadAsset("skeleton/human/${orientation.value}/skeleton.skel")
    )
    private val skeleton: Skeleton = Skeleton(skeletonData)
    private val skinSlots: Map<PawnSkeletonSlot, Slot> by lazy {
        skeleton.run {
            SkinPawnSkeletonSlot.allParts.associateWith { findSlot(it.value) }
        }
    }
    private val customizableSlots: Map<CustomizablePawnSkinSlot, SlotData> by lazy {
        skeleton.run {
            CustomizablePawnSkinSlot.allParts.associateWith {
                val slot = findSlot(it.value)
                SlotData(slot, slot.attachment as RegionAttachment)
            }
        }
    }
    private val slotsHiddenByArmor by lazy {
        mapOf(
            ArmorPawnSlot.Piece.Helmet to listOf(
                CustomizablePawnSkinSlot.Hair,
                CustomizablePawnSkinSlot.Ears.EarLeft,
                CustomizablePawnSkinSlot.Ears.EarRight
            )
        )
    }
    private val armorSlots: Map<ArmorPawnSlot.Piece, List<SlotData>> by lazy {
        skeleton.run {
            ArmorPawnSlot.pieces.map { (piece, slots) ->
                piece to slots.map { slot ->
                    val slot = findSlot(slot.value)
                    SlotData(slot, slot.attachment as RegionAttachment)
                }
            }.toMap()
        }
    }

    private val skeletonRenderer: SkeletonRenderer = SkeletonRenderer()
    private val stateData: AnimationStateData = AnimationStateData(skeletonData)
    private val animationState: AnimationState = AnimationState(stateData)

    private var stripped = false

    init {
        skeleton.setPosition(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2)
        skinSlots.forEach {
            it.value.data.color.set(defaultColor)
        }
        skeleton.setToSetupPose()
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        skeletonRenderer.setPremultipliedAlpha(true)
        animationState.setAnimation(0, Idle.value, true)

        strip()
    }

    fun strip() {
        stripped = true
        armorSlots.forEach { (piece, slots) ->
            slots.forEach { data ->
                data.slot.attachment = null
            }
            slotsHiddenByArmor[piece]?.forEach { data ->
                customizableSlots[data]?.let { slotData ->
                    slotData.slot.attachment = slotData.currentAttachment
                }
            }
        }
    }

    fun wear() {
        stripped = false
        armorSlots.forEach { (piece, slots) ->
            slots.forEach { data ->
                data.slot.attachment = data.currentAttachment
            }
            slotsHiddenByArmor[piece]?.forEach { data ->
                customizableSlots[data]?.let { slotData ->
                    slotData.slot.attachment = null
                }
            }
        }
    }

    fun toggleStrip() {
        if (stripped) {
            wear()
        } else {
            strip()
        }
    }

    val testArmor = TextureAtlas(loadAsset("armor/banditcaptain.atlas"))
    val headRegion = testArmor.findRegion("frontHead")

    fun setHelmet(region: TextureRegion) {
        armorSlots[ArmorPawnSlot.Piece.Helmet]?.forEach { slot ->
            val regionAttachment = RegionAttachment("newAttachment")
            regionAttachment.region = region
            slot.update(regionAttachment)
            slot.slot.attachment = regionAttachment
        }
    }

    context(deltaTime: Float, batch: SpriteBatch)
    override fun render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            setHelmet(headRegion)
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            toggleStrip()
        }
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        animationState.update(deltaTime)
        animationState.apply(skeleton)
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        skeleton.update(deltaTime)
        skeletonRenderer.draw(batch, skeleton)
    }
}