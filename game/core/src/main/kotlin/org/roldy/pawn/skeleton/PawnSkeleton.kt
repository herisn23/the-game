package org.roldy.pawn.skeleton

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.spine.*
import com.esotericsoftware.spine.attachments.Attachment
import com.esotericsoftware.spine.attachments.RegionAttachment
import kotlinx.serialization.Serializable
import org.roldy.ObjectRenderer
import org.roldy.asset.loadAsset
import org.roldy.asset.loadAtlasWithMeta
import org.roldy.pawn.ArmorWearablePawn
import org.roldy.pawn.CustomizablePawn
import org.roldy.pawn.skeleton.PawnSkeleton.Companion.hiddenSlotsDefault
import org.roldy.pawn.skeleton.attribute.*
import kotlin.properties.Delegates

class PawnSkeleton(
    val orientation: PawnSkeletonOrientation,
    private val defaultSkinColor: Color,
    private val defaultHairColor: Color
) : ObjectRenderer, ArmorWearablePawn, CustomizablePawn, StrippablePawn {
    companion object {
        val hiddenSlotsDefault = mapOf(
            CustomizablePawnSkinSlot.Hair to false,
            CustomizablePawnSkinSlot.EarLeft to false,
            CustomizablePawnSkinSlot.EarRight to false

        )
    }

    override var skinColor = defaultSkinColor
    override var hairColor = defaultHairColor

    private var hiddenSlots: Map<CustomizablePawnSkinSlot, Boolean> = hiddenSlotsDefault
    private val skeletonPath = "pawn/human/skeleton/${orientation.value}"
    private val skeletonAtlas: TextureAtlas =
        TextureAtlas(loadAsset("$skeletonPath/skeleton.atlas"))
    private val binarySkeleton: SkeletonBinary = SkeletonBinary(skeletonAtlas)
    private val skeletonData: SkeletonData = binarySkeleton.readSkeletonData(
        loadAsset("$skeletonPath/skeleton.skel")
    )
    private val skeleton: Skeleton = Skeleton(skeletonData)
    private val skinSlots: Map<PawnSkeletonSlot, Slot> by lazy {
        skeleton.run {
            SkinPawnSkeletonSlot.allParts.associateWith { findSlot(it.value) }
        }
    }
    private val customizableSlots: Map<CustomizablePawnSkinSlot, PawnCustomizationSlotData> by lazy {
        skeleton.run {
            CustomizablePawnSkinSlot.allParts.associateWith {
                val slot = findSlot(it.value)
                PawnCustomizationSlotData(slot, it, slot.attachment as RegionAttachment)
            }
        }
    }
    private val groupedArmorSlots: Map<ArmorPawnSlot.Piece, List<PawnArmorSlotData>> by lazy {
        skeleton.run {
            ArmorPawnSlot.pieces.map { (piece, slots) ->
                piece to slots.map { slotName ->
                    val slot = findSlot(slotName.value)
                    PawnArmorSlotData(slot, slotName, slot.attachment as RegionAttachment)
                }
            }.toMap()
        }
    }
    private val armorSlots: Map<ArmorPawnSlot, PawnArmorSlotData> by lazy {
        groupedArmorSlots.map { (pawnSlot, slots) ->
            slots
        }.flatten().associateBy { it.slotName }
    }

    private val skeletonRenderer: SkeletonRenderer = SkeletonRenderer()
    private val animationStateData: AnimationStateData = AnimationStateData(skeletonData)
    private val animationState: AnimationState = AnimationState(animationStateData)

    init {
        skeleton.setPosition(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2)
        skinSlots.forEach {
            it.value.data.color.set(defaultSkinColor)
        }
        skeleton.setToSetupPose()
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        skeletonRenderer.setPremultipliedAlpha(true)
        animationState.setAnimation(0, Idle.value, true)
        strip()
    }


    override fun strip() {
        armorSlots.values.forEach { data ->
            data.currentAttachment = null
        }
        showHidableSlots()
    }

    override fun setArmor(slot: ArmorPawnSlot, atlasData: PawnArmorSlotData.TextureAtlasData) {
        armorSlots[slot]?.let { slotData ->
            val regionName = slot.regionName(orientation)
            val regionAttachment = RegionAttachment(regionName)
            regionAttachment.region = atlasData.atlas.findRegion(regionName)
            slotData.update(regionAttachment, atlasData)
            hiddenSlots = slotData.hiddenSlotsState
            hideHidableSlots(slot)
        }
    }

    override fun removeArmor(slot: ArmorPawnSlot) {
        armorSlots[slot]?.let { slotData ->
            slotData.currentAttachment = null
        }
    }

    private fun hideHidableSlots(armorSlot: ArmorPawnSlot) {
        val piece = ArmorPawnSlot.slotPiece.getValue(armorSlot)
        CustomizablePawnSkinSlot.hideableSlotsByArmor[piece]?.forEach { slot ->
            val hidden = hiddenSlots.getValue(slot)
            customizableSlots[slot]?.let { data ->
                if (hidden) {
                    data.slot.attachment = null
                } else {
                    data.slot.attachment = data.currentAttachment
                }
            }
        }
    }

    private fun showHidableSlots() {
        hiddenSlots.forEach { (slot, _) ->
            customizableSlots[slot]?.let { data ->
                data.slot.attachment = data.currentAttachment
            }
        }
    }

    override fun customize(slot: CustomizablePawnSkinSlot, atlas: TextureAtlas) {
        customizableSlots[slot]?.let { slotData ->
            val currentAttachment = slotData.slot.attachment
            val regionAttachment = RegionAttachment(slotData.slotName.value)
            regionAttachment.region = slot.findRegion(orientation, atlas)
            slotData.update(regionAttachment)
            //if we customize while wearing armor then hide slot
            if (currentAttachment == null) {
                slotData.slot.attachment = null
            }
        }

    }

    override fun removeCustomization(slot: CustomizablePawnSkinSlot) {
        customizableSlots[slot]?.let { data ->
            data.currentAttachment = null
        }
    }

    private fun drawHairColor() {
        CustomizablePawnSkinSlot.hairSlots.forEach {
            customizableSlots[it]?.apply {
                slot.color.set(hairColor)
            }
        }
    }

    private fun drawSkinColor() {
        skinSlots.forEach {
            it.value.color.set(skinColor)
        }
    }


    context(deltaTime: Float, batch: SpriteBatch)
    override fun render() {
        drawHairColor()
        drawSkinColor()
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        animationState.update(deltaTime)
        animationState.apply(skeleton)
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        skeleton.update(deltaTime)
        skeletonRenderer.draw(batch, skeleton)
    }

    override fun dispose() {
        skeletonAtlas.dispose()
    }
}


class PawnArmorSlotData(slot: Slot, slotName: ArmorPawnSlot, originAttachment: RegionAttachment) :
    PawnSlotData<ArmorPawnSlot>(slot, slotName, originAttachment) {
    val hiddenSlotsState = hiddenSlotsDefault.toMutableMap()

    @Serializable
    data class Metadata(
        val name: String,
        val flags: Map<String, Boolean>
    )

    data class TextureAtlasData(
        val atlas: TextureAtlas,
        private val meta: List<Metadata>
    ) {
        val map = meta.associateBy { it.name }
        companion object Companion {
            fun load(relativePath: String) =
                loadAtlasWithMeta(relativePath).run {
                    TextureAtlasData(
                        first, second
                    )
                }
        }
        operator fun get(name: String) = map.getValue(name)
    }

    fun update(attachment: RegionAttachment, data: TextureAtlasData) {
        preUpdate(attachment, data)
        super.update(attachment)
    }

    fun preUpdate(attachment: RegionAttachment, data: TextureAtlasData) {
        val meta = data[attachment.name]
        meta.flags.forEach { (key, value) ->
            when (key) {
                "showEars" -> {
                    hiddenSlotsState[CustomizablePawnSkinSlot.EarLeft] = !value
                    hiddenSlotsState[CustomizablePawnSkinSlot.EarRight] = !value
                }

                "showHair" -> {
                    hiddenSlotsState[CustomizablePawnSkinSlot.Hair] = !value
                }
            }
        }

    }

}

class PawnCustomizationSlotData(slot: Slot, slotName: CustomizablePawnSkinSlot, originAttachment: RegionAttachment) :
    PawnSlotData<CustomizablePawnSkinSlot>(slot, slotName, originAttachment) {
}

abstract class PawnSlotData<T : PawnSkeletonSlot>(
    val slot: Slot,
    val slotName: T,
    private val originAttachment: RegionAttachment
) {
    var currentAttachment: Attachment? by Delegates.observable(originAttachment) { _, _, newValue ->
        slot.attachment = newValue
    }

    fun update(attachment: RegionAttachment) {
        attachment.x = originAttachment.x
        attachment.y = originAttachment.y
        attachment.rotation = originAttachment.rotation
        attachment.scaleX = originAttachment.scaleX
        attachment.scaleY = originAttachment.scaleY
        attachment.height = originAttachment.height
        attachment.width = originAttachment.width
        attachment.color.set(originAttachment.color)
        attachment.updateRegion()
        this.currentAttachment = attachment
    }
}
