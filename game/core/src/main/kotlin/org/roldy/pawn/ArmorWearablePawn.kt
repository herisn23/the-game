package org.roldy.pawn

import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.pawn.skeleton.attribute.ArmorPawnSlot

interface ArmorWearablePawn {
    fun setArmor(slot: ArmorPawnSlot, atlasData: ArmorAtlas)
    fun removeArmor(slot: ArmorPawnSlot)
}