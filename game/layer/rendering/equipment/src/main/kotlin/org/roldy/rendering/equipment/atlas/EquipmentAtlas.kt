package org.roldy.rendering.equipment.atlas

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.asset.AtlasLoader
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable

abstract class EquipmentAtlas(atlasPath: String) : AutoDisposableAdapter() {

    val atlas by disposable {
        AtlasLoader.load(atlasPath)
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