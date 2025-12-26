import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import org.roldy.gui.GUITexture
import org.roldy.gui.GUITextures
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.NinePatchDsl
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.ninePatch
import org.roldy.rendering.g2d.gui.ninePatchParams


@Scene2dCallbackDsl
context(gui: GuiContext)
fun <A> buttonRLBackground(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(
        gui.region { Button_RL_Background },
        ninePatchParams(left = 50, right = 50, top = 30, bottom = 30),
        element
    )

@Scene2dCallbackDsl
context(gui: GuiContext)
fun <A> buttonRLForeground(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(
        gui.region { Button_RL_Foreground },
        ninePatchParams(left = 50, right = 50, top = 30, bottom = 30),
        element
    )

@Scene2dCallbackDsl
context(gui: GuiContext)
fun <A> buttonRLHover(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { Button_RL_Hover }, left = 56, right = 56, element = element)

@Scene2dCallbackDsl
context(gui: GuiContext)
fun <A> buttonRLOverlay1(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { Button_RL_Overlay1 }, 0, 0, 13, 0, element)

@Scene2dCallbackDsl
context(gui: GuiContext)
fun <A> buttonRLOverlay2(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { Button_RL_Overlay2 }, 30, 46, 27, 30, element)

@Scene2dCallbackDsl
context(gui: GuiContext)
fun <A> buttonRSBackgroundGrayscale(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { Button_RS_Background_Grayscale }, ninePatchParams(18), element)

@Scene2dCallbackDsl
context(gui: GuiContext)
fun <A> buttonRSBorder2(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { Button_RS_Border2 }, ninePatchParams(16), element)

@Scene2dCallbackDsl
context(gui: GuiContext)
fun <A> tooltipBackground(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { Tooltip_Background }, ninePatchParams(25), element)

@NinePatchDsl
context(gui: GuiContext)
fun popupNinePatch(texture: GUITextures.() -> GUITexture) =
    ninePatch(gui.textures.texture().region(), ninePatchParams(60)) {
        this
    }

@NinePatchDsl
context(gui: GuiContext)
fun <A> generalContainer(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { General_Container }, ninePatchParams(32), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> generalContainer2(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { General_Container_2 }, ninePatchParams(80), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> generalContainerBorder(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { General_Container_Border }, ninePatchParams(18), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> generalContainerBorder2(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { General_Container_Border_2 }, ninePatchParams(52), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> generalContainerBorder3(element: @NinePatchDsl NinePatchDrawable.() -> A): A =
    ninePatch(gui.region { General_Container_Border_3 }, ninePatchParams(40), element)


@NinePatchDsl
context(gui: GuiContext)
fun <A> windowHeaderBackground(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Window_Header_Background }, ninePatchParams(top = 2, bottom = 2), element)


@NinePatchDsl
context(gui: GuiContext)
fun <A> windowHeaderBorder(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Window_Header_Border }, ninePatchParams(left = 18, right = 18), element)


@NinePatchDsl
context(gui: GuiContext)
fun <A> windowHeaderMiddleGlow(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Window_Header_MiddleGlow }, ninePatchParams(), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> windowHeaderOrnament(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Window_Header_Ornaments }, ninePatchParams(left = 500, right = 2), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> windowHeaderOrnamentRight(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    windowHeaderOrnament {
        // Flip horizontally
//        patch.scale(-1f, 1f)
//        patch.texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge)
        val newPatch = NinePatch(
            gui.region { Window_Header_Ornaments },
            patch.rightWidth.toInt(),
            patch.leftWidth.toInt(),
            patch.topHeight.toInt(),
            patch.bottomHeight.toInt()
        )
        newPatch.setPadding(
            patch.padRight,
            patch.padLeft,
            patch.padTop,
            patch.padBottom
        )
        NinePatchDrawable(newPatch).element()
    }


@NinePatchDsl
context(gui: GuiContext)
fun <A> windowHeaderOrnamentEffect(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Window_Header_Ornaments_Effect }, ninePatchParams(), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> windowHeaderOverlay(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Window_Header_Overlay }, ninePatchParams(42), element)


@NinePatchDsl
context(gui: GuiContext)
fun <A> slotHover(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Slot_Hover }, ninePatchParams(26), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> separatorHorizontal(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Separator_Horizontal }, ninePatchParams(left = 10, right = 10), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> separatorVertical(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Separator_Horizontal }, ninePatchParams(top = 8, bottom = 8), element)

@NinePatchDsl
context(gui: GuiContext)
fun <A> separatorVertical2(element: @NinePatchDsl NinePatchDrawable.() -> A) =
    ninePatch(gui.region { Separator_Horizontal }, ninePatchParams(top = 12, bottom = 12), element)