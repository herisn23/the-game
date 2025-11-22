package org.roldy.pawn

import org.roldy.equipment.atlas.weapon.ShieldAtlas

interface ShieldWearer {
    fun setShield(atlas: ShieldAtlas)
    fun removeShield()
}