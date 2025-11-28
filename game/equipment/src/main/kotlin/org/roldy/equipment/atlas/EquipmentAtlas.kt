package org.roldy.equipment.atlas

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.asset.loadAsset

abstract class EquipmentAtlas(atlasPath: String) {

    val atlas by lazy {
        TextureAtlas(loadAsset(atlasPath))
    }

    fun dispose() {
        atlas.dispose()
    }

    fun findRegion(regionName: String): TextureAtlas.AtlasRegion =
        atlas.findRegion(regionName)

    abstract class Region(
        val name: String,
        val atlas: EquipmentAtlas
    ) {
        val region by lazy {
            atlas.findRegion(name)
        }
    }
}
