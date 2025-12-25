package org.roldy.gui

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.utils.drawable

class GUITexture(
    val name: String,
    val atlas: TextureAtlas
) {

    fun drawable() =
        region().drawable()

    fun region() =
        atlas.findRegion(name)
}