package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.i18n.I18N
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.g2d.gameFont
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.i18n.I18NContext


data class GUIColors(
    val default: Color,
    val tint: Color,
)

data class GuiContext(
    val atlas: TextureAtlas,
    val colors: GUIColors,
    override val i18n: I18N,
    val font: (Int, FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit) -> BitmapFont
) : I18NContext

@Scene2dDsl
fun Gui.gui(scale: Float = 1f, build: context(GuiContext) (@Scene2dDsl KStage).(GuiContext) -> Unit): KStage {
    val atlas by disposable { AtlasLoader.gui }
    val colors = GUIColors(
        default = Color.RED,
        tint = Color.valueOf("FFEDCFFF")
    )
    val bundle = I18N()
    val guiContext = GuiContext(atlas, colors, bundle) { size, initialize ->
        gameFont(size = size, initialize = initialize).disposable()
    }

    val stage by disposable {
        context(guiContext) {
            stage(scale) {
                build(guiContext)
            }
        }
    }
    return stage
}