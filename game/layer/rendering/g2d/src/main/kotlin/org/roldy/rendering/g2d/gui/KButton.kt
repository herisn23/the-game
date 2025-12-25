package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import kotlin.contracts.ExperimentalContracts

interface KButton {
    fun addListener(listener: EventListener): Boolean
}



@Scene2dCallbackDsl
@OptIn(ExperimentalContracts::class)
fun KButton.onClick(onClick: () -> Unit) {
    addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            onClick()
        }
    })
}
