package org.roldy.gui.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import org.roldy.core.utils.get
import org.roldy.gui.GuiContext
import org.roldy.rendering.g2d.gui.*

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.tilePopup(): KStandardPopup =
    popupNinePatch {
        val anchor = gui.atlas["OrnamentOutside"].let(::Image).apply {
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

@NinePatchDsl
context(gui: GuiContext)
private fun <S> popupNinePatch(element: @NinePatchDsl NinePatchDrawable.() -> S) =
    ninePatch(gui.atlas["Tooltip_Background"], NinePatchParams(25, 25, 25, 25), element)