package org.roldy.gui

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import org.roldy.core.utils.get
import org.roldy.rendering.g2d.gui.*

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.worldAnchoredPopup(): KClampingPopup {
    val topLeft = popupNinePatch("Popup_Top_Left").tint(gui.colors.default)
    val topRight = popupNinePatch("Popup_Top_Right").tint(gui.colors.default)
    val bottomLeft = popupNinePatch("Popup_Bottom_Left").tint(gui.colors.default)
    val bottomRight = popupNinePatch("Popup_Bottom_Right").tint(gui.colors.default)
    return clampingPopup(
        KClampingPopup.Backgrounds(
            topLeft,
            topRight,
            bottomLeft,
            bottomRight
        )
    ) {
        padding = 10f
    }
}

@NinePatchDsl
context(gui: GuiContext)
private fun popupNinePatch(name: String) =
    ninePatch(gui.atlas[name], NinePatchParams(60, 60, 60, 60)) {
        this
    }


@NinePatchDsl
context(gui: GuiContext)
private fun <S> popupNinePatch(element: @NinePatchDsl NinePatchDrawable.() -> S) =
    ninePatch(gui.atlas["Tooltip_Background"], NinePatchParams(25, 25, 25, 25), element)