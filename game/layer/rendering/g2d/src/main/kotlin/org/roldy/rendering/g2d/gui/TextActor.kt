package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions

interface TextActor {
    fun updateText()
}


fun TextActor.updateTextAction(): Action = Actions.forever(
    Actions.run {
        updateText()
    }
)