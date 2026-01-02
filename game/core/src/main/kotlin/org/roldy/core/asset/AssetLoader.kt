package org.roldy.core.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas

fun loadAsset(name: String): FileHandle =
    Gdx.files.internal(name)


object AtlasLoader {
    val settlements get() = load("environment/Settlements.atlas")
    val tileDecorationNormal get() = load("environment/TileDecorationNormal.atlas")
    val tileDecorationTropic get() = load("environment/TileDecorationTropic.atlas")
    val tileDecorationCold get() = load("environment/TileDecorationCold.atlas")
    val roads get() = load("environment/Roads.atlas")
    val harvestable get() = load("environment/Harvestable.atlas")
    val gui get() = load("ui/GUI.atlas")
    val craftingIcons get() = load("ui/CraftingResourceIcons.atlas")
    val underTile get() = load("terrain/UnderHex.atlas")

    fun load(name: String) =
        TextureAtlas(loadAsset(name))
}
