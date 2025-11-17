package org.roldy

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import kotlin.reflect.KProperty

operator fun (() -> TextureAtlas).getValue(thisRef: Any?, property: KProperty<*>): TextureAtlas.AtlasRegion {
    return this().getValue(thisRef, property)
}

operator fun TextureAtlas.getValue(thisRef: Any?, property: KProperty<*>): TextureAtlas.AtlasRegion {
    return findRegion(property.name)
}

