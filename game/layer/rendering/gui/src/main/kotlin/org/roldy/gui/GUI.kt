package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.i18n.I18N
import org.roldy.core.utils.hex
import org.roldy.core.x
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.g2d.gameFont
import org.roldy.rendering.g2d.gui.Gui
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIStage
import org.roldy.rendering.g2d.gui.el.stage
import org.roldy.rendering.g2d.gui.i18n.I18NContext

data class GUIColors(
    val primary: Color,
    val secondary: Color,
    val button: Color,
    val window: Color,
    val primaryText: Color,
    val secondaryText: Color,
)

data class GuiContext(
    val colors: GUIColors,
    val textures: GUITextures,
    override val i18n: I18N,
    val font: (Int, FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit) -> BitmapFont
) : I18NContext {
    lateinit var stage: Stage
    operator fun <A> invoke(get: GUITextures.() -> A) =
        textures.get()

    fun region(get: GUITextures.() -> GUITexture): TextureAtlas.AtlasRegion =
        this(get).region()

    fun drawable(get: GUITextures.() -> GUITexture): TextureRegionDrawable =
        this(get).drawable()

    override fun stage(): Stage = stage
}

@Scene2dDsl
fun Gui.gui(
    scale: Float = 1f,
    build: context(GuiContext) (@Scene2dDsl UIStage).(GuiContext) -> Unit
): UIStage {
    val atlas by disposable { AtlasLoader.gui }
    val colors = GUIColors(
        primary = hex("FF463D"),//FF463D
        secondary = hex("FF6363FF"),
        button = hex("FFEDCF"),
        window = hex("c7ac66"),
        primaryText = hex("8B7E6EFF"),
        secondaryText = hex("C1B197FF")
    )
    val bundle = I18N()
    val guiContext = GuiContext(colors, GUITextures(atlas), bundle) { size, initialize ->
        gameFont(size = size, color = colors.button, initialize = initialize).disposable()
    }

    val stage by disposable {
        context(guiContext) {
            stage(scale, 3840 x 2160) {

                guiContext.stage = this
                build(guiContext)
            }
        }
    }
    return stage
}