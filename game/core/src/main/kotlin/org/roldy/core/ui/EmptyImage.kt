package org.roldy.core.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.roldy.core.disposable.AutoDisposable
import org.roldy.core.utils.alpha


fun AutoDisposable.pixmap(
    color: Color = alpha(1f),
    width: Int = 1,
    height: Int = 1,
    closure: TextureRegionDrawable.() -> Unit = {}
): TextureRegionDrawable = Pixmap(width, height, Pixmap.Format.RGBA8888).run {
    setColor(color)
    fill()
    TextureRegionDrawable(Texture(this).disposable()).also {
        dispose()
        it.setPadding(-10f)
    }.also(closure)
}

fun pixmapTexture(
    color: Color = alpha(1f),
    width: Int = 1,
    height: Int = 1,
    closure: Texture.() -> Unit = {}
): Texture = Pixmap(width, height, Pixmap.Format.RGBA8888).run {
    setColor(color)
    fill()
    Texture(this).also(closure)
}

fun Texture.copy(
    color: Color = alpha(1f),
): Texture = Pixmap(width, height, Pixmap.Format.RGBA8888).run {
    textureData.prepare()
    val originalPixmap: Pixmap = textureData.consumePixmap()

    for (y in 0..<height) {
        for (x in 0..<width) {
            val pixel: Int = originalPixmap.getPixel(
                x,
                y
            )
            val pixelColor = Color(pixel)
            pixelColor.mul(color)
            drawPixel(x, y, Color.rgba8888(pixelColor))
        }
    }
    Texture(this).also {
        dispose()
    }
}