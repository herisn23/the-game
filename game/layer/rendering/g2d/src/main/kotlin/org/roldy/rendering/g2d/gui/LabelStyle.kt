package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle

fun labelStyle(init: LabelStyle.() -> Unit) =
    LabelStyle().also {
        init(it)
    }