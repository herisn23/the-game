package org.roldy.rendering.pawn.customization

import org.roldy.rendering.equipment.atlas.armor.ArmorAtlas
import org.roldy.rendering.pawn.skeleton.attribute.PawnArmorSlot

interface ArmorWearer {
    fun setArmor(piece: PawnArmorSlot.Piece, atlasData: ArmorAtlas)
    fun removeArmor(slot: PawnArmorSlot)
}