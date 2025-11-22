package org.roldy.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.pawn.skeleton.attribute.UnderWearSlot

class PawnUnderWearSlotData(slot: Slot, slotName: UnderWearSlot, originAttachment: RegionAttachment) :
    PawnSlotData<UnderWearSlot>(
        slot, slotName,
        originAttachment
    )