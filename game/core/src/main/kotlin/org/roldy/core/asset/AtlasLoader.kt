package org.roldy.core.asset

import com.badlogic.gdx.graphics.g2d.TextureAtlas

object AtlasLoader {
    val gui by lazy { load("ui/GUI.atlas") }
    val craftingIcons by lazy { load("ui/CraftingResourceIcons.atlas") }
    val terrain by lazy { load("terrain/Terrain.atlas") }
    val terrainTileSet by lazy { loadAsset("terrain/Tileset.png") }
    val sun by lazy { loadAsset("billboards/sun_glow.png") }
    val moon by lazy { loadAsset("billboards/celestialObject_23.png") }
    val planet by lazy { loadAsset("billboards/celestialObject_18.png") }

    fun load(name: String) =
        TextureAtlas(loadAsset(name))
}
