package org.roldy.gui.general.button

import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.rendering.g2d.gui.el.UITextButton

class TextButtonActions(
    val button: UITextButton,
    val gui: GuiContext
) {
    fun setText(text: String?) {
        button.setText(text)
    }

    fun setText(text: TextManager) {
        context(gui) {
            button.setText(text.getText)
        }
    }
}