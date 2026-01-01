package org.roldy.core.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas

fun loadAsset(name: String): FileHandle =
    Gdx.files.internal(name)


object AtlasLoader {
    val settlements get() = load("environment/Settlements.atlas")
    val roads get() = load("environment/Roads.atlas")
    val mines get() = load("environment/Mines.atlas")
    val gui get() = load("GUI.atlas")
    val craftingIcons get() = load("CraftingResourceIcons.atlas")

    fun load(name: String) =
        TextureAtlas(loadAsset(name))
}
