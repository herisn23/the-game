package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Action

interface TextActor {
    fun updateText()
}


fun TextActor.updateTextAction(): Action =
    function {
        updateText()
    }.forever()