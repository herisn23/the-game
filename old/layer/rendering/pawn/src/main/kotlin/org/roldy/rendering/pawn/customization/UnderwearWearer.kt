package org.roldy.rendering.pawn.customization

import com.badlogic.gdx.graphics.Color
import org.roldy.rendering.equipment.atlas.customization.UnderWearAtlas

interface UnderwearWearer {
    var underwearColor: Color

    fun setUnderwear(atlas: UnderWearAtlas)
    fun removeUnderwear()
}