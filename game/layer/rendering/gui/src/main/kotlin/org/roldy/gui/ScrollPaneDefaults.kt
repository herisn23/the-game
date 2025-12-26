package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.scrollPaneStyle

private const val scrollSize = 18

context(gui: GuiContext)
val defaultScrollPaneStyle
    get() =
        scrollPaneStyle {
            hScrollKnob = emptyImage(Color.BLACK alpha .4f, height = scrollSize)
            hScroll = emptyImage(gui.colors.secondaryText alpha .2f, height = scrollSize)

            vScrollKnob = emptyImage(Color.BLACK alpha .4f, width = scrollSize)
            vScroll = emptyImage(gui.colors.secondaryText alpha .2f, width = scrollSize)
        }
