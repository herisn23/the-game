package org.roldy.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSkinSlot

class PawnCustomizationSlotData(slot: Slot, slotName: CustomizablePawnSkinSlot, originAttachment: RegionAttachment) :
    PawnSlotData<CustomizablePawnSkinSlot>(slot, slotName, originAttachment) {
}
