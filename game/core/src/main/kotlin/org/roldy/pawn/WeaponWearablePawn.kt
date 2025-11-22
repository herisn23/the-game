package org.roldy.pawn

import org.roldy.equipment.atlas.weapon.WeaponRegion
import org.roldy.pawn.skeleton.attribute.WeaponPawnSlot

interface WeaponWearablePawn {
    fun setWeapon(slot: WeaponPawnSlot, region: WeaponRegion)
    fun removeWeapon(slot: WeaponPawnSlot)
}