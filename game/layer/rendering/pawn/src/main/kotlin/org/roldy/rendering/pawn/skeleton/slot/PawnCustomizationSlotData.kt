package org.roldy.rendering.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.rendering.pawn.skeleton.attribute.CustomizablePawnSlotBody

class PawnCustomizationSlotData(slot: Slot, slotName: CustomizablePawnSlotBody, originAttachment: RegionAttachment) :
    PawnSlotData<CustomizablePawnSlotBody>(slot, slotName, originAttachment) {
}
