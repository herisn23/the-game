package org.roldy.pawn

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.pawn.skeleton.attribute.ArmorPawnSlot

interface ArmorWearablePawn {
    fun setArmor(slot: ArmorPawnSlot, atlas: TextureAtlas)
    fun removeArmor(slot: ArmorPawnSlot)
}