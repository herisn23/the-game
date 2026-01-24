package org.roldy.core.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas

fun loadAsset(name: String): FileHandle =
    Gdx.files.internal(name)


object AtlasLoader {
    val tileEnvironment get() = load("tiles/Environment.atlas")
    val tiles get() = load("tiles/Tiles.atlas")
    val roads get() = load("tiles/Roads.atlas")
    val hexOutline get() = load("tiles/Outline.atlas")
    val gui get() = load("ui/GUI.atlas")
    val craftingIcons get() = load("ui/CraftingResourceIcons.atlas")

    fun load(name: String) =
        TextureAtlas(loadAsset(name))
}
