package org.roldy.gui.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import org.roldy.gui.GuiContext
import org.roldy.gui.tooltipBackground
import org.roldy.rendering.g2d.gui.KStandardPopup
import org.roldy.rendering.g2d.gui.KWidget
import org.roldy.rendering.g2d.gui.Scene2dDsl
import org.roldy.rendering.g2d.gui.standardPopup

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.tilePopup(): KStandardPopup =
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