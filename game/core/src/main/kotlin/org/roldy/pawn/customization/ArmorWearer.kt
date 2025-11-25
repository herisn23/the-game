package org.roldy.pawn

import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.pawn.skeleton.attribute.PawnArmorSlot

interface ArmorWearer {
    fun setArmor(piece: PawnArmorSlot.Piece, atlasData: ArmorAtlas)
    fun removeArmor(slot: PawnArmorSlot)
}