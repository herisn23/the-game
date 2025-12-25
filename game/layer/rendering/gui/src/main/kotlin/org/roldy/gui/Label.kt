package org.roldy.gui

import org.roldy.rendering.g2d.gui.*

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.label(text: () -> String, fontSize: Int = 25, autoupdate: Boolean = true) =
    label(text, labelStyle {
        font = gui.font(fontSize) {}

        /* Markup example:
            [#FF0000]Health:[] 100
            [#00FF00]Energy:[] 50
         */
        font.data.markupEnabled = true
    }) {
        if (autoupdate)
            addAction(updateTextAction())
    }