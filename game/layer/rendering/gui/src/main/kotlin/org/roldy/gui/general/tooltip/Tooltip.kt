package org.roldy.gui.general.tooltip

import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.gui.general.label
import org.roldy.gui.tooltipBackground
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIStructuredTooltip
import org.roldy.rendering.g2d.gui.el.structuredTooltip


@Scene2dDsl
context(gui: GuiContext)
fun Actor.textTooltip(
    text: TextManager
) = tooltipBackground {
    structuredTooltip(this) {
        padding = 10f
        addListener(this)
        content {
            label(text)
        }
    }
}

@Scene2dDsl
context(gui: GuiContext)
fun Actor.tooltip(): UIStructuredTooltip = tooltipBackground {
    structuredTooltip(this) {
        padding = 10f
        addListener(this)
    }
}
