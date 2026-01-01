package org.roldy.gui

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.roldy.core.utils.drawable

class GUITexture(
    val name: String,
    val atlas: TextureAtlas
) {

    fun drawable(): TextureRegionDrawable =
        region().drawable()

    fun region(): TextureAtlas.AtlasRegion =
        atlas.findRegion(name)

}