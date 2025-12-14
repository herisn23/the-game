package org.roldy.core.utils

import com.badlogic.gdx.graphics.g2d.TextureAtlas


infix operator fun TextureAtlas.get(regionName: String): TextureAtlas.AtlasRegion =
    findRegion(regionName)