package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type

data class InputListenerProxy(
    val event: InputEvent
) {
    internal var consumed = false

    @Scene2dInputDsl
    fun isDescendantOf(other: Actor): Boolean {
        if (event.relatedActor != null) {
            return event.relatedActor.isDescendantOf(other)
        }
        return false
    }

    @Scene2dInputDsl
    fun whenDescendantOf(other: Actor, consume: (Actor) -> Unit) {
        if (isDescendantOf(other)) {
            consume(event.relatedActor)
        }
    }

    @Scene2dInputDsl
    fun whenNotDescendantOf(other: Actor, consume: () -> Unit) {
        if (!isDescendantOf(other)) {
            consume()
        }
    }
}

@Scene2dCallbackDsl
fun Actor.addInputListener(consuming: Boolean = true, listener: InputListenerProxy.() -> Unit): EventListener {
    val eventListener = inputListener(consuming, listener)
    addListener(eventListener)
    return eventListener
}
@Scene2dCallbackDsl
fun Stage.addInputListener(consuming: Boolean = true, listener: InputListenerProxy.() -> Unit): EventListener {
    val eventListener = inputListener(consuming, listener)
    addListener(eventListener)
    return eventListener
}

@Scene2dCallbackDsl
fun inputListener(consuming: Boolean = true, listener: InputListenerProxy.() -> Unit): EventListener =
    EventListener {
        processEvent(it, consuming, listener)
    }

private fun processEvent(event: Event, consuming: Boolean = true, listener: InputListenerProxy.() -> Unit): Boolean =
    if (event is InputEvent) {
        val proxy = InputListenerProxy(event)
        listener(proxy)
        (proxy.consumed && consuming).also { consumed ->
//            val actor = event.listenerActor?.let{"actor"}?:"null"
//            println("consumed: ${event.type} - ${actor}: ${event.listenerActor.name} -> $consumed")
        }
    } else {
        false
    }

fun InputListenerProxy.type(vararg type: Type, consume: InputListenerProxy.() -> Unit): InputListenerProxy {
    if (type.isNotEmpty() && type.contains(event.type)) {
        consume()
        consumed = true
    }
    return this
}

fun InputListenerProxy.block(vararg type: Type): InputListenerProxy {
    type(*type) {

    }
    return this
}

fun InputListenerProxy.button(type: Type, vararg code: Int, consume: InputListenerProxy.() -> Unit) =
    type(type) {
        consumed = false
        if (code.isNotEmpty() && code.contains(event.button)) {
            consume()
        }
    }

fun InputListenerProxy.key(type: Type, vararg code: Int, consume: InputListenerProxy.() -> Unit) =
    type(type) {
        consumed = false
        if (code.isNotEmpty() && code.contains(event.keyCode)) {
            consume()
        }
    }