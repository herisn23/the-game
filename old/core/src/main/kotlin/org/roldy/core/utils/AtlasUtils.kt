package org.roldy.core.utils

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable


infix operator fun TextureAtlas.get(regionName: String): TextureAtlas.AtlasRegion =
    findRegion(regionName)

infix fun TextureAtlas.drawable(regionName: String) =
    get(regionName).drawable()

fun TextureAtlas.AtlasRegion.drawable() =
    TextureRegionDrawable(this)

fun TextureRegion.new() =
    TextureRegion(this)