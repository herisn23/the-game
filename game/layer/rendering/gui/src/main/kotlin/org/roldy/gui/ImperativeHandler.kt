package org.roldy.gui

import org.roldy.core.cast
import org.roldy.rendering.g2d.gui.Scene2dCallbackDsl
import kotlin.properties.Delegates
import kotlin.reflect.KClass

interface ImperativeActions

typealias DelegatedImperativeActionListener<Value> = (Value) -> Unit


@Scene2dCallbackDsl
class ImperativeActionDelegate : ImperativeActions {

    val valueListeners =
        mutableMapOf<KClass<*>, DelegatedImperativeActionListener<*>>()
    val stored: MutableMap<KClass<*>, Any> = mutableMapOf()

    inline fun <reified Value> onChange(noinline listener: DelegatedImperativeActionListener<Value>) {
        valueListeners[Value::class] = listener
    }

    inline fun <reified Value> get() =
        stored[Value::class] as Value

    inline fun <reified Value> set(value: Value) {
        value?.let { v ->
            valueListeners[v::class]
                .cast<DelegatedImperativeActionListener<Value>> {
                    it(value)
                    stored[v::class] = value
                }
        }

    }
}

@Scene2dCallbackDsl
class ImperativeActionValue<Value>(initial: Value) : ImperativeActions {
    var listener: DelegatedImperativeActionListener<Value>? = null

    var value: Value by Delegates.observable(initial) { _, _, newValue ->
        listener?.invoke(newValue)
    }

    fun onChange(listener: (Value) -> Unit) {
        this.listener = listener
    }
}

class ImperativeHandler<R, Actions : ImperativeActions>(
    val ref: R,
    private val actions: Actions,
) : ImperativeActions by actions {
    operator fun invoke(nest: Actions.() -> Unit) {
        actions.nest()
    }
}

@Scene2dCallbackDsl
fun <Value, R> value(init: Value, ref: ImperativeActionValue<Value>.() -> R) =
    imperative(ImperativeActionValue(init), ref)

@Scene2dCallbackDsl
fun <R> delegate(ref: ImperativeActionDelegate.() -> R) =
    imperative(ImperativeActionDelegate(), ref)

@Scene2dCallbackDsl
fun delegate() =
    ImperativeActionDelegate()

@Scene2dCallbackDsl
fun <A : ImperativeActions, R> imperative(actions: A, ref: A.() -> R): ImperativeHandler<R, A> =
    ImperativeHandler(actions.ref(), actions)