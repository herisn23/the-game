package org.roldy.rendering.pawn.customization

import org.roldy.rendering.equipment.atlas.weapon.WeaponRegion
import org.roldy.rendering.pawn.skeleton.attribute.PawnWeaponSlot

interface WeaponWearer {
    fun setWeapon(slot: PawnWeaponSlot, region: WeaponRegion)
    fun removeWeapon(slot: PawnWeaponSlot)
}