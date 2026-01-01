package org.roldy.gui

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.utils.drawable
import org.roldy.core.utils.get
import org.roldy.data.mine.harvestable.Harvestable

enum class CraftingIconTexturesType {
    Normal, Background;

    fun suffix(): String =
        when(this) {
            Normal -> ""
            Background -> "_b"
        }
}

class CraftingIconTextures(
    val atlas: TextureAtlas
) {


    fun region(harvestable: Harvestable, type: CraftingIconTexturesType) =
        atlas["${harvestable.key}${type.suffix()}"]

    fun drawable(harvestable: Harvestable, type: CraftingIconTexturesType) =
        region(harvestable, type).drawable()
}