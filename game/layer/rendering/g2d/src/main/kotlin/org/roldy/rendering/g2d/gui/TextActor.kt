package org.roldy.rendering.g2d.gui

interface TextActor {
    val text: () -> String
    fun updateText()

}