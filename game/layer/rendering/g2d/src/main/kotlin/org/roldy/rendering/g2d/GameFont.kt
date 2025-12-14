package org.roldy.rendering.g2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import org.roldy.core.asset.loadAsset

fun gameFont(
    size: Int = 16,
    border: Float = 0f,
    color: Color = Color.WHITE,
    borderColor: Color = Color.WHITE,
    initialize: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {}
): BitmapFont {
    val generator = FreeTypeFontGenerator(loadAsset("font/DragonHunter-9Ynxj.otf"))

    val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = size  // Font size in pixels
    parameter.color = color
    parameter.borderWidth = border  // Optional outline
    parameter.borderColor = borderColor
    initialize(parameter)
    return generator.generateFont(parameter).also {
        generator.dispose()
    }
}