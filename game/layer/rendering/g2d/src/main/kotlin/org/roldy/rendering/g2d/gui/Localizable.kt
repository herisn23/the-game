package org.roldy.rendering.g2d.gui

interface Localizable {
    val text: ()->String

    fun updateText()
}