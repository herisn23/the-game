package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.i18n.I18N
import org.roldy.core.utils.alpha
import org.roldy.core.utils.hex
import org.roldy.core.utils.new
import org.roldy.core.x
import org.roldy.rendering.g2d.FontStyle
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.g2d.gameFont
import org.roldy.rendering.g2d.gui.Gui
import org.roldy.rendering.g2d.gui.I18NContext
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIStage
import org.roldy.rendering.g2d.gui.el.stage
import org.roldy.rendering.g2d.pixmap

data class GUIColors(
    val primary: Color,
    val secondary: Color,
    val window: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val tertiaryText: Color,
    val quaternaryText: Color,
    val disabled: Color,
    val progressBar: Color
)

class WorldGuiContext(
    val craftingIcons: CraftingIconTextures,
    colors: GUIColors,
    textures: GUITextures,
    i18n: I18N,
    gui: Gui,
    font: (FontStyle, Int, FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit) -> BitmapFont,
) : GuiContext(colors, textures, i18n, gui, font)

class DefaultGuiContext(
    colors: GUIColors,
    textures: GUITextures,
    i18n: I18N,
    gui: Gui,
    font: (FontStyle, Int, FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit) -> BitmapFont
) : GuiContext(colors, textures, i18n, gui, font)

abstract class GuiContext(
    val colors: GUIColors,
    val textures: GUITextures,
    override val i18n: I18N,
    val gui: Gui,
    val font: (FontStyle, Int, FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit) -> BitmapFont
) : I18NContext {
    lateinit var stage: UIStage
    operator fun <A> invoke(get: GUITextures.() -> A) =
        textures.get()

    override val disposable: AutoDisposable = gui

    fun region(new: Boolean = false, get: GUITextures.() -> GUITexture): TextureRegion =
        this(get).region().run {
            if (new) {
                new()
            } else {
                this
            }
        }

    fun drawable(get: GUITextures.() -> GUITexture): TextureRegionDrawable =
        this(get).drawable()

    fun drawable(color: Color = Color.WHITE, get: GUITextures.() -> GUITexture): Drawable =
        this(get).drawable().tint(color)

    fun pixmap(
        color: Color = alpha(1f),
        width: Int = 1,
        height: Int = 1,
        closure: TextureRegionDrawable.() -> Unit = {}
    ) =
        gui.pixmap(color, width, height, closure)

    override fun stage(): UIStage = stage
}

var colorSchema = GUIColors(
    primary = hex("FF463D"),//FF463D
    secondary = hex("FF6363FF"),
    window = hex("d6ba5d"),
    primaryText = hex("FFEDCF"),
    secondaryText = hex("dbcab9"),
    tertiaryText = hex("C1B197FF"),
    quaternaryText = hex("8B7E6EFF"),
    disabled = Color.GRAY,
    progressBar = hex("4A6433FF"),
)

@Scene2dDsl
fun <C : GuiContext> Gui.gui(
    scale: Float = 1f,
    guiContext: C,
    build: context(C) (@Scene2dDsl UIStage).(C) -> Unit
): UIStage {
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

fun Gui.createDefaultGuiContext(): DefaultGuiContext {
    val atlas by disposable { AtlasLoader.gui }
    val colors = colorSchema
    val bundle = I18N()
    return DefaultGuiContext(
        colors,
        GUITextures(atlas),
        bundle,
        this
    ) { style, size, initialize ->
        gameFont(size = size, style = style, initialize = initialize).disposable()
    }
}

fun Gui.createWorldGuiContext(craftingIcons: TextureAtlas): WorldGuiContext {
    val atlas by disposable { AtlasLoader.gui }
    val colors = colorSchema
    val bundle = I18N()
    return WorldGuiContext(
        CraftingIconTextures(craftingIcons),
        colors,
        GUITextures(atlas),
        bundle,
        this
    ) { style, size, initialize ->
        gameFont(size = size, style = style, initialize = initialize).disposable()
    }
}