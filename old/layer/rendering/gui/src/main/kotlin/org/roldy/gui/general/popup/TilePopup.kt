package org.roldy.gui.general.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import org.roldy.gui.GuiContext
import org.roldy.gui.tooltipBackground
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.el.UIStandardPopup
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.standardPopup

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.tilePopup(): UIStandardPopup =
    tooltipBackground {
        val anchor = gui.region { OrnamentOutside }.let(::Image).apply {
            rotation = 90f
        }
        standardPopup(
            this,
            anchor
        ) {
            anchorOffsetY = -20f
            anchorOffsetX = anchor.width * .5f + 10f
            padding = 10f
        }
    }