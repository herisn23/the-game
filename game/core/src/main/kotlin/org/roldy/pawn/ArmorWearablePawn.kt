package org.roldy.pawn

import org.roldy.pawn.skeleton.PawnArmorSlotData
import org.roldy.pawn.skeleton.attribute.ArmorPawnSlot

interface ArmorWearablePawn {
    fun setArmor(slot: ArmorPawnSlot, atlasData: PawnArmorSlotData.TextureAtlasData)
    fun removeArmor(slot: ArmorPawnSlot)
}