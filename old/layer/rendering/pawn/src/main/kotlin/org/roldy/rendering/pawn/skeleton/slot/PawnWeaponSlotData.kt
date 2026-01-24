package org.roldy.rendering.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.rendering.pawn.skeleton.attribute.PawnWeaponSlot

class PawnWeaponSlotData(slot: Slot, slotName: PawnWeaponSlot, originAttachment: RegionAttachment) :
    PawnSlotData<PawnWeaponSlot>(
        slot, slotName,
        originAttachment
    )
