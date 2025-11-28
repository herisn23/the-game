package org.roldy.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSlotBody

class PawnCustomizationSlotData(slot: Slot, slotName: CustomizablePawnSlotBody, originAttachment: RegionAttachment) :
    PawnSlotData<CustomizablePawnSlotBody>(slot, slotName, originAttachment) {
}
