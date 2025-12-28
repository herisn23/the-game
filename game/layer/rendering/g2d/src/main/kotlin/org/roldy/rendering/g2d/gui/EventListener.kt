package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent


data class InputListenerProxy(
    val event: InputEvent
) {
    internal var consumed = false
}

@Scene2dCallbackDsl
fun Actor.inputListener(consuming: Boolean = true, listener: InputListenerProxy.() -> Unit) =
    addListener {
        if (it is InputEvent) {
            val proxy = InputListenerProxy(it)
            listener(proxy)
            proxy.consumed && consuming
        } else {
            false
        }
    }

fun InputListenerProxy.type(vararg type: InputEvent.Type, consume: () -> Unit) {
    if (type.isNotEmpty() && type.contains(event.type)) {
        consume()
        consumed = true
    }
}