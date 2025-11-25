package org.roldy.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.pawn.skeleton.attribute.PawnWeaponSlot

class PawnWeaponSlotData(slot: Slot, slotName: PawnWeaponSlot, originAttachment: RegionAttachment) :
    PawnSlotData<PawnWeaponSlot>(
        slot, slotName,
        originAttachment
    )
