package org.roldy.gui.general.tooltip

import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.gui.GuiContext
import org.roldy.gui.popupNinePatch
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIContextualTooltip
import org.roldy.rendering.g2d.gui.el.contextualTooltip

@Scene2dDsl
context(gui: GuiContext)
fun Actor.tooltip(
    init: (@Scene2dDsl UIContextualTooltip).() -> Unit = {}
): UIContextualTooltip {
    val topLeft = popupNinePatch { Popup_Top_Left }.tint(gui.colors.primary)
    val topRight = popupNinePatch { Popup_Top_Right }.tint(gui.colors.primary)
    val bottomLeft = popupNinePatch { Popup_Bottom_Left }.tint(gui.colors.primary)
    val bottomRight = popupNinePatch { Popup_Bottom_Right }.tint(gui.colors.primary)
    return contextualTooltip(
        UIContextualTooltip.Backgrounds(
            topLeft,
            topRight,
            bottomLeft,
            bottomRight
        )
    ) {
        minWidth(128f)
        minHeight(128f)
        addListener(this)
        init()
    }
}

