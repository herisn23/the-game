package org.roldy.gui.popup

import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.KClampingPopup
import org.roldy.rendering.g2d.gui.KWidget
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.clampingPopup
import popupNinePatch

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.globalPopup(): KClampingPopup {
    val topLeft = popupNinePatch { Popup_Top_Left }.tint(gui.colors.primary)
    val topRight = popupNinePatch { Popup_Top_Right }.tint(gui.colors.primary)
    val bottomLeft = popupNinePatch { Popup_Bottom_Left }.tint(gui.colors.primary)
    val bottomRight = popupNinePatch { Popup_Bottom_Right }.tint(gui.colors.primary)
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

