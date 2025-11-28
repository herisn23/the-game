package org.roldy.pawn.skeleton

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.spine.*
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.core.ObjectRenderer
import org.roldy.core.asset.loadAsset
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.CustomizationAtlas
import org.roldy.equipment.atlas.customization.UnderWearAtlas
import org.roldy.equipment.atlas.weapon.ShieldAtlas
import org.roldy.equipment.atlas.weapon.WeaponRegion
import org.roldy.pawn.customization.*
import org.roldy.pawn.skeleton.attribute.*
import org.roldy.pawn.skeleton.slot.PawnArmorSlotData
import org.roldy.pawn.skeleton.slot.PawnCustomizationSlotData
import org.roldy.pawn.skeleton.slot.PawnUnderWearSlotData
import org.roldy.pawn.skeleton.slot.PawnWeaponSlotData


class PawnSkeletonData private constructor(
    orientation: PawnSkeletonOrientation
) {
    val skeletonPath = "pawn/human/skeleton"
    val textureAtlas by lazy {
        TextureAtlas(loadAsset("$skeletonPath/${orientation.skeletonName}.atlas"))
    }
    val binarySkeleton: SkeletonBinary by lazy {
        SkeletonBinary(textureAtlas)
    }
    val skeletonData: SkeletonData by lazy {
        binarySkeleton.readSkeletonData(
            loadAsset("$skeletonPath/${orientation.skeletonName}.skel")
        )
    }

    companion object {
        val instance by lazy {
            PawnSkeletonOrientation.all.associateWith(::PawnSkeletonData)
        }

    }
}

class PawnSkeleton(
    val orientation: PawnSkeletonOrientation,
    private val pawnSkeletonData: PawnSkeletonData,
    private val defaultSkinColor: Color,
    private val defaultHairColor: Color,
    private val defaultUnderwearColor: Color,
) : ObjectRenderer, ArmorWearer, Customizable, Strippable, WeaponWearer, UnderwearWearer, ShieldWearer {
    companion object {
        val hiddenSlotsDefault = mapOf(
            CustomizablePawnSlotBody.Hair to false,
            CustomizablePawnSlotBody.EarLeft to false,
            CustomizablePawnSlotBody.EarRight to false

        )
    }

    override var skinColor = defaultSkinColor
    override var hairColor = defaultHairColor
    override var underwearColor = defaultUnderwearColor

    private var hiddenSlots: Map<CustomizablePawnSlotBody, Boolean> = hiddenSlotsDefault


    internal val skeleton: Skeleton = Skeleton(pawnSkeletonData.skeletonData).apply {
        scaleX = orientation.scaleX
        scaleY = orientation.scaleY
    }

    private val skinSlots: Map<PawnBodySkeletonSlot, Slot> by lazy {
        skeleton.run {
            PawnBodySkeletonSlot.allParts.mapNotNull {
                findSlot(it.capitalizedName)?.run {
                    it to this
                }
            }.toMap()
        }
    }

    private val underwearSlots: Map<UnderWearSlotBody, PawnUnderWearSlotData> by lazy {
        skeleton.run {
            UnderWearSlotBody.allParts.associateWith {
                val slot = findSlot(it.capitalizedName)
                PawnUnderWearSlotData(slot, it, slot.attachment as RegionAttachment)
            }
        }
    }
    private val customizableSlots: Map<CustomizablePawnSlotBody, PawnCustomizationSlotData> by lazy {
        skeleton.run {
            CustomizablePawnSlotBody.allParts.mapNotNull {
                findSlot(it.capitalizedName)?.let { slot ->
                    it to PawnCustomizationSlotData(slot, it, slot.attachment as RegionAttachment)
                }
            }.toMap()
        }
    }
    private val groupedArmorSlots: Map<PawnArmorSlot.Piece, List<PawnArmorSlotData>> by lazy {
        skeleton.run {
            PawnArmorSlot.pieces.map { (piece, slots) ->
                piece to slots.mapNotNull { slotName ->
                    findSlot(slotName.capitalizedName)?.let { slot ->
                        PawnArmorSlotData(slot, slotName, slot.attachment as RegionAttachment)
                    }
                }
            }.toMap()
        }
    }
    private val armorSlots: Map<PawnArmorSlot, PawnArmorSlotData> by lazy {
        groupedArmorSlots.map { (pawnSlot, slots) ->
            slots
        }.flatten().associateBy { it.slotName }
    }

    private val weaponSlots by lazy {
        skeleton.run {
            PawnWeaponSlot.all.associateWith { slotName ->
                val slot = findSlot(slotName.capitalizedName)
                PawnWeaponSlotData(slot, slotName, slot.attachment as RegionAttachment)
            }
        }
    }

    private val skeletonRenderer: SkeletonRenderer = SkeletonRenderer()
    private val animationStateData: AnimationStateData = AnimationStateData(pawnSkeletonData.skeletonData)
    val animator: PawnAnimator = PawnAnimator(animationStateData, this)

    val animation: PawnAnimation = animator

    init {
        skeleton.setPosition(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2)
        skinSlots.forEach {
            it.value.data.color.set(defaultSkinColor)
        }
        skeleton.setToSetupPose()
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        skeletonRenderer.setPremultipliedAlpha(true)
        clean()
        animator.idle()
    }


    override fun strip() {
        armorSlots.values.forEach(PawnArmorSlotData::remove)
        showHidableSlots()
    }

    private fun clean() {
        strip()
        weaponSlots.values.forEach(PawnWeaponSlotData::remove)
    }

    override fun setArmor(piece: PawnArmorSlot.Piece, atlasData: ArmorAtlas) {
        groupedArmorSlots[piece]?.forEach { slotData ->
            val regionName = slotData.slotName.regionName(orientation)
            val regionAttachment = RegionAttachment(regionName)
            regionAttachment.region = atlasData.atlas.findRegion(regionName)
            slotData.update(regionAttachment, atlasData)
            hiddenSlots = slotData.hiddenSlotsState
            hideHidableSlots(slotData.slotName)
        }
    }

    override fun removeArmor(slot: PawnArmorSlot) {
        armorSlots[slot]?.remove()
    }

    override fun setWeapon(slot: PawnWeaponSlot, region: WeaponRegion) {
        weaponSlots[slot]?.let { slotData ->
            val regionAttachment = RegionAttachment(region.name)
            regionAttachment.region = region.region
            slotData.update(regionAttachment)
        }
    }

    override fun removeWeapon(slot: PawnWeaponSlot) {
        weaponSlots[slot]?.remove()
    }

    private fun hideHidableSlots(armorSlot: PawnArmorSlot) {
        val piece = PawnArmorSlot.slotPiece.getValue(armorSlot)
        CustomizablePawnSlotBody.hideableSlotsByArmor[piece]?.forEach { slot ->
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

    override fun customize(slot: CustomizablePawnSlotBody, atlasData: CustomizationAtlas) {
        customizableSlots[slot]?.let { slotData ->
            val currentAttachment = slotData.slot.attachment
            val regionAttachment = RegionAttachment(slotData.slotName.capitalizedName)
            regionAttachment.region = slot.findRegion(orientation, atlasData.atlas)
            slotData.update(regionAttachment)
            //if we customize while wearing armor then hide slot
            if (currentAttachment == null) {
                slotData.slot.attachment = null
            }
        }

    }

    override fun removeCustomization(slot: CustomizablePawnSlotBody) {
        customizableSlots[slot]?.remove()
    }

    override fun setUnderwear(atlas: UnderWearAtlas) {
        underwearSlots.forEach { (slot, slotData) ->
            val regionAttachment = RegionAttachment(slotData.slotName.capitalizedName)
            regionAttachment.region = atlas.findRegion(slot.regionName(orientation))
            slotData.update(regionAttachment)
        }
    }

    override fun removeUnderwear() {
        underwearSlots.forEach { (_, data) ->
            data.remove()
        }
    }

    override fun setShield(atlas: ShieldAtlas) {
        weaponSlots[PawnWeaponSlot.Shield]?.run {
            val regionAttachment = RegionAttachment(slotName.capitalizedName)
            val regionName = when (orientation) {
                Left, Front -> "front"
                else -> "back"
            }
            regionAttachment.region = atlas.findRegion(regionName)
            update(regionAttachment)
        }
    }

    override fun removeShield() {
        weaponSlots[PawnWeaponSlot.Shield]?.remove()
    }

    private fun drawHairColor() {
        CustomizablePawnSlotBody.hairSlots.forEach {
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

    private fun drawUnderWearColor() {
        underwearSlots.forEach {
            it.value.slot.color.set(underwearColor)
        }
    }

    context(deltaTime: Float)
    fun animate() {
        animator.update(deltaTime)
    }

    context(deltaTime: Float, batch: SpriteBatch)
    override fun render() {
        drawHairColor()
        drawSkinColor()
        drawUnderWearColor()
        skeleton.updateWorldTransform(Skeleton.Physics.update)
        skeleton.update(deltaTime)
        skeletonRenderer.draw(batch, skeleton)
    }
}


