package org.roldy.core.ui

import com.badlogic.gdx.scenes.scene2d.Action

interface TextActor {
    fun updateText()
}


fun TextActor.updateTextAction(): Action =
    function {
        updateText()
    }.forever()