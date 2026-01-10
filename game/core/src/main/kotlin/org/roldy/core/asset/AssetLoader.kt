package org.roldy.core.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas

fun loadAsset(name: String): FileHandle =
    Gdx.files.internal(name)


object AtlasLoader {
    val tileDecorations get() = load("tiles/Decors.atlas")
    val tiles get() = load("tiles2/Tiles.atlas")
    val roads get() = load("tiles2/Roads.atlas")
    val gui get() = load("ui/GUI.atlas")
    val craftingIcons get() = load("ui/CraftingResourceIcons.atlas")

    fun load(name: String) =
        TextureAtlas(loadAsset(name))
}
