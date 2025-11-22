package org.roldy.pawn

import com.badlogic.gdx.graphics.Color
import org.roldy.equipment.atlas.underwear.UnderWearAtlas

interface UnderwearPawn {
    var underwearColor: Color

    fun setUnderWear(atlas: UnderWearAtlas)
    fun removeUnderwear()
}