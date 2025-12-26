package org.roldy.gui.tooltip

import com.badlogic.gdx.scenes.scene2d.Actor
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.gui.label
import org.roldy.gui.tooltipBackground
import org.roldy.rendering.g2d.gui.KStructuredTooltip
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.structuredTooltip


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
fun Actor.tooltip(): KStructuredTooltip = tooltipBackground {
    structuredTooltip(this) {
        padding = 10f
        addListener(this)
    }
}
