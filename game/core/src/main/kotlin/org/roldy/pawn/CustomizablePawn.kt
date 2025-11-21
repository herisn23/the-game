package org.roldy.pawn

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSkinSlot

interface CustomizablePawn {

    fun customize(slot: CustomizablePawnSkinSlot, atlas: TextureAtlas)
    var hairColor: Color
    var skinColor: Color
}