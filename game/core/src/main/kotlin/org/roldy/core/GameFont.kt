package org.roldy.core


import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import org.roldy.core.asset.loadAsset

enum class FontStyle {
    Default, Marking
}

const val LatinCharacters =
    "ĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſ" +
            "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿ"

fun gameFont(
    size: Int = 16,
    style: FontStyle = FontStyle.Default,
    border: Float = 0f,
    color: Color = Color.WHITE,
    borderColor: Color = Color.WHITE,
    initialize: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {}
): BitmapFont {
    val generator = FreeTypeFontGenerator(
        loadAsset(
            when (style) {
                Default -> "font/Default.ttf"
                Marking -> "font/Marking.ttf"
            }
        )
    )

    val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = size  // Font size in pixels
    parameter.color = color
    parameter.borderWidth = border  // Optional outline
    parameter.borderColor = borderColor
    parameter.characters += LatinCharacters
    initialize(parameter)
    return generator.generateFont(parameter).also {
        generator.dispose()
    }
}