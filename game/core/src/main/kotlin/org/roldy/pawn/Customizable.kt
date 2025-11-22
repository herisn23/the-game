package org.roldy.pawn

import com.badlogic.gdx.graphics.Color
import org.roldy.equipment.atlas.customization.CustomizationAtlas
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSkinSlot

interface Customizable {

    fun customize(slot: CustomizablePawnSkinSlot, atlasData: CustomizationAtlas)
    fun removeCustomization(slot: CustomizablePawnSkinSlot)
    var hairColor: Color
    var skinColor: Color
}