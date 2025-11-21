package org.roldy.g2d.atlas

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import kotlin.reflect.KProperty

typealias AtlasAccessor = () -> TextureAtlas

class LazyAtlasRegion(
    private val atlas: AtlasAccessor
) {
    private var region: TextureRegion? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): TextureRegion {
        if (region == null) {
            region = atlas().findRegion(property.name)
        }
        return region!!
    }
}

fun region(atlas: AtlasAccessor) =
    LazyAtlasRegion(atlas)
