package org.roldy.core.asset

import com.badlogic.gdx.graphics.g2d.TextureAtlas

object AtlasLoader {
    val gui by lazy { load("ui/GUI.atlas") }
    val craftingIcons by lazy { load("ui/CraftingResourceIcons.atlas") }

    fun load(name: String) =
        TextureAtlas(loadAsset(name))
}
