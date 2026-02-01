package org.roldy.core

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture

fun flipTexture(tex: Texture): Texture {
    // Flip texture vertically
    if (!tex.textureData.isPrepared) {
        tex.textureData.prepare()
    }
    val pixmap = tex.textureData.consumePixmap()

    // Flip Y
    val flipped = Pixmap(pixmap.width, pixmap.height, pixmap.format)
    for (y in 0 until pixmap.height) {
        for (x in 0 until pixmap.width) {
            flipped.drawPixel(x, pixmap.height - 1 - y, pixmap.getPixel(x, y))
        }
    }

    val flippedTex = Texture(flipped)
    flippedTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    flippedTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

    pixmap.dispose()
    flipped.dispose()
    return flippedTex
}