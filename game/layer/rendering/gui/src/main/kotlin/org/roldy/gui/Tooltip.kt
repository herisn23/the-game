package org.roldy.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import org.roldy.core.i18n.t
import org.roldy.core.utils.get
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.i18n.localizable


@Scene2dDsl
context(gui: GuiContext)
fun Actor.simpleTooltip(
    text: () -> String
) = tooltipNinePatch {
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
fun Actor.tooltip(): KStructuredTooltip = tooltipNinePatch {
    structuredTooltip(this) {
        padding = 10f
        addListener(this)
    }
}

@NinePatchDsl
context(gui: GuiContext)
private fun <S> tooltipNinePatch(element: @NinePatchDsl NinePatchDrawable.() -> S) =
    ninePatch(gui.atlas["Tooltip_Background"], NinePatchParams(25, 25, 25, 25), element)