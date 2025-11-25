package org.roldy.pawn

import com.badlogic.gdx.graphics.Color
import org.roldy.equipment.atlas.customization.CustomizationAtlas
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSlotBody

interface Customizable {

    fun customize(slot: CustomizablePawnSlotBody, atlasData: CustomizationAtlas)
    fun removeCustomization(slot: CustomizablePawnSlotBody)
    var hairColor: Color
    var skinColor: Color
}