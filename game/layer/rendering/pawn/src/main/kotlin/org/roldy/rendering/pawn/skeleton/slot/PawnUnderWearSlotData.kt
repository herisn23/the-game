package org.roldy.rendering.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.rendering.pawn.skeleton.attribute.UnderWearSlotBody

class PawnUnderWearSlotData(slot: Slot, slotName: UnderWearSlotBody, originAttachment: RegionAttachment) :
    PawnSlotData<UnderWearSlotBody>(
        slot, slotName,
        originAttachment
    )