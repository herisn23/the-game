package org.roldy.rendering.g2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.roldy.core.utils.alpha


fun emptyImage(
    color: Color = alpha(1f),
    width: Int = 1,
    height: Int = 1,
    closure: TextureRegionDrawable.() -> Unit = {}
) = Pixmap(width, height, Pixmap.Format.RGBA8888).run {
    setColor(color)
    fill()
    TextureRegionDrawable(Texture(this)).also {
        dispose()
        it.setPadding(-10f)
    }
}