package org.roldy.gui.popup

import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.gui.GuiContext
import org.roldy.gui.popupNinePatch
import org.roldy.rendering.g2d.gui.KContextualPopup
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.contextualPopup

@Scene2dDsl
context(gui: GuiContext)
fun Actor.popup(
    init: (@Scene2dCallbackDsl KContextualPopup).() -> Unit = {}
): KContextualPopup {
    val topLeft = popupNinePatch { Popup_Top_Left }.tint(gui.colors.primary)
    val topRight = popupNinePatch { Popup_Top_Right }.tint(gui.colors.primary)
    val bottomLeft = popupNinePatch { Popup_Bottom_Left }.tint(gui.colors.primary)
    val bottomRight = popupNinePatch { Popup_Bottom_Right }.tint(gui.colors.primary)
    return contextualPopup(
        KContextualPopup.Backgrounds(
            topLeft,
            topRight,
            bottomLeft,
            bottomRight
        )
    ) {
        minWidth(128f)
        minHeight(128f)
//        padding = 10f
        addListener(this)
        init()
    }
}

