package org.roldy.rendering.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.rendering.equipment.atlas.armor.ArmorAtlas
import org.roldy.rendering.pawn.skeleton.PawnSkeleton.Companion.hiddenSlotsDefault
import org.roldy.rendering.pawn.skeleton.attribute.CustomizablePawnSlotBody
import org.roldy.rendering.pawn.skeleton.attribute.PawnArmorSlot

class PawnArmorSlotData(slot: Slot, slotName: PawnArmorSlot, originAttachment: RegionAttachment) :
    PawnSlotData<PawnArmorSlot>(slot, slotName, originAttachment) {
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
                    hiddenSlotsState[CustomizablePawnSlotBody.EarLeft] = !value
                    hiddenSlotsState[CustomizablePawnSlotBody.EarRight] = !value
                }

                "showHair" -> {
                    hiddenSlotsState[CustomizablePawnSlotBody.Hair] = !value
                }
            }
        }

    }

}
