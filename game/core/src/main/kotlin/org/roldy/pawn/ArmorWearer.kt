package org.roldy.pawn

import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.pawn.skeleton.attribute.ArmorPawnSlot

interface ArmorWearer {
    fun setArmor(piece: ArmorPawnSlot.Piece, atlasData: ArmorAtlas)
    fun removeArmor(slot: ArmorPawnSlot)
}