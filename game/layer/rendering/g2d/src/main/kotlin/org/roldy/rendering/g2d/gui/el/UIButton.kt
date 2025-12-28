package org.roldy.rendering.g2d.gui.el

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import kotlin.contracts.ExperimentalContracts

interface UIButton {
    fun addListener(listener: EventListener): Boolean
}


@Scene2dCallbackDsl
@OptIn(ExperimentalContracts::class)
fun UIButton.onClick(button:Int = Input.Buttons.LEFT, onClick: (InputEvent) -> Unit) {
    addListener(object : ClickListener(button) {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            onClick(event)
        }
    })
}
