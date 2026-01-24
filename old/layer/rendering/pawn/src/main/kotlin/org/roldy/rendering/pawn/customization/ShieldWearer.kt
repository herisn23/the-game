package org.roldy.rendering.pawn.customization

import org.roldy.rendering.equipment.atlas.weapon.ShieldAtlas

interface ShieldWearer {
    fun setShield(atlas: ShieldAtlas)
    fun removeShield()
}