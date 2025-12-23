package org.roldy.rendering.g2d.gui

interface Text {
    val text: () -> String
    fun updateText()
}