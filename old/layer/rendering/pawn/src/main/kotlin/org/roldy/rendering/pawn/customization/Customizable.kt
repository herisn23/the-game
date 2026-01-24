package org.roldy.rendering.pawn.customization

import com.badlogic.gdx.graphics.Color
import org.roldy.rendering.equipment.atlas.customization.CustomizationAtlas
import org.roldy.rendering.pawn.skeleton.attribute.CustomizablePawnSlotBody

interface Customizable {

    fun customize(slot: CustomizablePawnSlotBody, atlasData: CustomizationAtlas)
    fun removeCustomization(slot: CustomizablePawnSlotBody)
    var hairColor: Color
    var skinColor: Color
}