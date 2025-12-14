package org.roldy.gui

import com.badlogic.gdx.scenes.scene2d.Actor


fun Actor.stretch(
    width: Float,
    height: Float,
    stretch: Float = 0f,
    left: Float = stretch,
    right: Float = stretch,
    top: Float = stretch,
    bottom: Float = stretch
) {

    setSize(width - right * 2, height - top * 2)
    setPosition(left, bottom)
}