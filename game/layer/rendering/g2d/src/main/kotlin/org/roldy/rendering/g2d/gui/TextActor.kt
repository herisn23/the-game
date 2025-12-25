package org.roldy.rendering.g2d.gui

interface TextActor {
    fun updateText()
}


fun TextActor.updateTextAction() = action {
    updateText()
}