package org.roldy.rendering.g2d.gui

import org.roldy.core.cast
import kotlin.properties.Delegates

interface ImperativeActions

interface ImperativeFunction<Data, R>

interface ImperativeValue<V>

typealias ImperativeValueHandler<Value> = (Value) -> Unit

typealias ImperativeActionHandler<Data, R> = (Data) -> R

typealias Value<Ref, V> = ImperativeHandler<Ref, ImperativeActionValue<V>>

typealias Delegate<Ref> = ImperativeHandler<Ref, ImperativeActionDelegate>

@Scene2dCallbackDsl
class ImperativeActionDelegate : ImperativeActions {

    val valueListeners =
        mutableMapOf<ImperativeValue<*>, MutableList<ImperativeValueHandler<*>>>()

    val functionListeners =
        mutableMapOf<ImperativeFunction<*, *>, MutableList<ImperativeActionHandler<*, *>>>()

    val stored: MutableMap<ImperativeValue<*>, Any?> = mutableMapOf()

    fun <Value> value(value: ImperativeValue<Value>, listener: ImperativeValueHandler<Value>) {
        valueListeners.getOrPut(value) { mutableListOf() }.add(listener)
    }

    fun <D, R, A : ImperativeFunction<D, R>> function(action: A, listener: ImperativeActionHandler<D, R>) {
        functionListeners.getOrPut(action) { mutableListOf() }.add(listener)
    }


    inline fun <reified Value> get(value: ImperativeValue<Value>) =
        stored[value].cast<Value>()

    fun <R, D, A : ImperativeFunction<D, R>> call(action: A, data: D) =
        functionListeners[action]?.forEach {
            it.cast<ImperativeActionHandler<D, R>>().invoke(data)
        }

    fun <Value> set(valueType: ImperativeValue<Value>, value: Value) {
        stored[valueType] = value as? Any
        valueListeners[valueType]?.forEach {
            it.cast<ImperativeValueHandler<Value>> {
                it(value)
            }
        }


    }

    operator fun <D, R> ImperativeFunction<D, R>.invoke(data: D) =
        call(this, data)

    operator fun <D, R> ImperativeFunction<D, R>.invoke(callback: (D) -> R) {
        function(this, callback)
    }
}

@Scene2dCallbackDsl
class ImperativeActionValue<Value>(initial: Value) : ImperativeActions {
    var listener: ImperativeValueHandler<Value>? = null

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
) : ImperativeActions {
    operator fun invoke(nest: Actions.() -> Unit) {
        actions.nest()
    }
}

@Scene2dCallbackDsl
fun <Value, R> value(init: Value, ref: ImperativeActionValue<Value>.() -> R) =
    imperative(ImperativeActionValue(init), ref)

@Scene2dCallbackDsl
fun <R> delegate(ref: ImperativeActionDelegate.() -> R): ImperativeHandler<R, ImperativeActionDelegate> =
    imperative(ImperativeActionDelegate(), ref)

@Scene2dCallbackDsl
fun delegate() =
    ImperativeActionDelegate()

@Scene2dCallbackDsl
fun <A : ImperativeActions, R> imperative(actions: A, ref: A.() -> R): ImperativeHandler<R, A> =
    ImperativeHandler(actions.ref(), actions)