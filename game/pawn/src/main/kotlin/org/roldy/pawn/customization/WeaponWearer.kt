package org.roldy.pawn.customization

import org.roldy.equipment.atlas.weapon.WeaponRegion
import org.roldy.pawn.skeleton.attribute.PawnWeaponSlot

interface WeaponWearer {
    fun setWeapon(slot: PawnWeaponSlot, region: WeaponRegion)
    fun removeWeapon(slot: PawnWeaponSlot)
}