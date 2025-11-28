package org.roldy.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.pawn.skeleton.PawnSkeleton.Companion.hiddenSlotsDefault
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSlotBody
import org.roldy.pawn.skeleton.attribute.PawnArmorSlot

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
