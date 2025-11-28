package org.roldy.pawn.customization

import com.badlogic.gdx.graphics.Color
import org.roldy.equipment.atlas.customization.UnderWearAtlas

interface UnderwearWearer {
    var underwearColor: Color

    fun setUnderwear(atlas: UnderWearAtlas)
    fun removeUnderwear()
}