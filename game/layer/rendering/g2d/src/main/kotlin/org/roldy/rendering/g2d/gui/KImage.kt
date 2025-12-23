package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable


@Scene2dDsl
class KImage : Image {
    constructor(patch: NinePatchDrawable): super(patch)
}