package org.roldy.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.pawn.skeleton.PawnSkeleton.Companion.hiddenSlotsDefault
import org.roldy.pawn.skeleton.attribute.ArmorPawnSlot
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSkinSlot

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
