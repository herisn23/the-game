package org.roldy.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.pawn.skeleton.attribute.WeaponPawnSlot

class PawnWeaponSlotData(slot: Slot, slotName: WeaponPawnSlot, originAttachment: RegionAttachment) :
    PawnSlotData<WeaponPawnSlot>(
        slot, slotName,
        originAttachment
    )
