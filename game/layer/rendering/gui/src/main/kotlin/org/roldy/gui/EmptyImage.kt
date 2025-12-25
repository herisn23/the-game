package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable


fun emptyImage(color: Color) = Pixmap(1, 1, Pixmap.Format.RGBA8888).run {
    setColor(color)
    fill()
    TextureRegionDrawable(Texture(this)).also {
        dispose()
    }
}