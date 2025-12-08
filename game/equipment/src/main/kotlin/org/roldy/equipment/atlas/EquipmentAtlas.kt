package org.roldy.equipment.atlas

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.asset.loadAsset
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable

abstract class EquipmentAtlas(atlasPath: String): AutoDisposableAdapter() {

    val atlas by disposable {
        TextureAtlas(loadAsset(atlasPath))
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
