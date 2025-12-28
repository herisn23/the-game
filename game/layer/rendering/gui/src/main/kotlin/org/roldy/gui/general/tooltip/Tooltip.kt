package org.roldy.gui.general.tooltip

import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.gui.GuiContext
import org.roldy.gui.popupNinePatch
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIContextualTooltip
import org.roldy.rendering.g2d.gui.el.UIContextualTooltipContent
import org.roldy.rendering.g2d.gui.el.contextualTooltip


/**
 * Sub tooltip
 */
@Scene2dDsl
context(gui: GuiContext)
fun UIContextualTooltipContent.tooltip(
    actor: Actor,
    init: (@Scene2dDsl UIContextualTooltip).() -> Unit = {}
): UIContextualTooltip =
    actor.tooltip(root, init)


/**
 * Root tooltip
 */
@Scene2dDsl
context(gui: GuiContext)
fun Actor.tooltip(
    parent: UIContextualTooltip? = null,
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
        parent?.nestedTooltip = this
        minWidth(128f)
        minHeight(128f)
        addListener(this)
        init()
    }
}

