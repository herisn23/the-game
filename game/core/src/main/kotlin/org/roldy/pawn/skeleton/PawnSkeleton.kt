package org.roldy.pawn.skeleton

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.spine.*
import com.esotericsoftware.spine.attachments.Attachment
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.ObjectRenderer
import org.roldy.asset.loadAsset
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.CustomizationAtlas
import org.roldy.equipment.atlas.weapon.WeaponRegion
import org.roldy.pawn.ArmorWearablePawn
import org.roldy.pawn.CustomizablePawn
import org.roldy.pawn.WeaponWearablePawn
import org.roldy.pawn.skeleton.PawnSkeleton.Companion.hiddenSlotsDefault
import org.roldy.pawn.skeleton.attribute.*
import kotlin.properties.Delegates

class PawnSkeleton(
    val orientation: PawnSkeletonOrientation,
    private val defaultSkinColor: Color,
    private val defaultHairColor: Color
) : ObjectRenderer, ArmorWearablePawn, CustomizablePawn, StrippablePawn, WeaponWearablePawn {
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

    private val weaponSlots by lazy {
        skeleton.run {
            WeaponPawnSlot.allParts.associateWith { slotName ->
                val slot = findSlot(slotName.value)
                PawnWeaponSlotData(slot, slotName, slot.attachment as RegionAttachment)
            }
        }
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
        armorSlots.values.forEach(PawnArmorSlotData::remove)
        showHidableSlots()
    }

    override fun setArmor(slot: ArmorPawnSlot, atlasData: ArmorAtlas) {
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
        armorSlots[slot]?.remove()
    }

    override fun setWeapon(slot: WeaponPawnSlot, region: WeaponRegion) {
        weaponSlots[slot]?.let { slotData ->
            val regionAttachment = RegionAttachment(region.name)
            regionAttachment.region = region.region
            slotData.update(regionAttachment)
        }
    }

    override fun removeWeapon(slot: WeaponPawnSlot) {
        weaponSlots[slot]?.remove()
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

    override fun customize(slot: CustomizablePawnSkinSlot, atlasData: CustomizationAtlas) {
        customizableSlots[slot]?.let { slotData ->
            val currentAttachment = slotData.slot.attachment
            val regionAttachment = RegionAttachment(slotData.slotName.value)
            regionAttachment.region = slot.findRegion(orientation, atlasData.atlas)
            slotData.update(regionAttachment)
            //if we customize while wearing armor then hide slot
            if (currentAttachment == null) {
                slotData.slot.attachment = null
            }
        }

    }

    override fun removeCustomization(slot: CustomizablePawnSkinSlot) {
        customizableSlots[slot]?.remove()
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


class PawnWeaponSlotData(slot: Slot, slotName: WeaponPawnSlot, originAttachment: RegionAttachment) :
    PawnSlotData<WeaponPawnSlot>(
        slot, slotName,
        originAttachment
    )

class PawnArmorSlotData(slot: Slot, slotName: ArmorPawnSlot, originAttachment: RegionAttachment) :
    PawnSlotData<ArmorPawnSlot>(slot, slotName, originAttachment) {
    val hiddenSlotsState = hiddenSlotsDefault.toMutableMap()

    fun update(attachment: RegionAttachment, data: ArmorAtlas) {
        preUpdate(attachment, data)
        super.update(attachment)
    }

    fun preUpdate(attachment: RegionAttachment, data: ArmorAtlas) {
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

    fun remove() {
        currentAttachment = null
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
