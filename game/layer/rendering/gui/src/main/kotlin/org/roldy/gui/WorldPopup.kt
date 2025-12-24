package org.roldy.gui

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import org.roldy.core.utils.get
import org.roldy.rendering.g2d.gui.*

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.worldPopup() = popupNinePatch {
    val topLeft = popupNinePatch("Popup_Top_Left")
    val topRight = popupNinePatch("Popup_Top_Right")
    val bottomLeft = popupNinePatch("Popup_Bottom_Left")
    val bottomRight = popupNinePatch("Popup_Bottom_Right")
    popup(
        KPopup.Backgrounds(
            topLeft.tint(gui.colors.default),
            topRight.tint(gui.colors.default),
            bottomLeft.tint(gui.colors.default),
            bottomRight.tint(gui.colors.default)
        )
    ) {
        padding = 20f
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