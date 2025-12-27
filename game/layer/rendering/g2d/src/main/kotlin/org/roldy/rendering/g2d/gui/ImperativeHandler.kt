package org.roldy.rendering.g2d.gui

import org.roldy.core.cast
import kotlin.properties.Delegates
import kotlin.reflect.KClass

interface ImperativeActions

interface ImperativeAction<Data, R>

typealias ImperativeValueHandler<Value> = (Value) -> Unit

typealias ImperativeActionHandler<Data, R> = (Data) -> R

typealias Value<Ref, V> = ImperativeHandler<Ref, ImperativeActionValue<V>>

typealias Delegate<Ref> = ImperativeHandler<Ref, ImperativeActionDelegate>

@Scene2dCallbackDsl
class ImperativeActionDelegate : ImperativeActions {

    val valueListeners =
        mutableMapOf<KClass<*>, ImperativeValueHandler<*>>()

    val actionListeners =
        mutableMapOf<ImperativeAction<*, *>, ImperativeActionHandler<*, *>>()

    val stored: MutableMap<KClass<*>, Any?> = mutableMapOf()

    inline fun <reified Value> value(noinline listener: ImperativeValueHandler<Value>) {
        valueListeners[Value::class] = listener
    }

    fun <D, R, A : ImperativeAction<D, R>> action(action: A, listener: ImperativeActionHandler<D, R>) {
        actionListeners[action] = listener
    }


    inline fun <reified Value> get() =
        stored[Value::class] as Value

    fun <R, D, A : ImperativeAction<D, R>> call(action: A, data: D) =
        actionListeners[action]?.cast<ImperativeActionHandler<D, R>>()?.invoke(data)

    inline fun <reified Value> set(value: Value) {
        valueListeners[Value::class]
            .cast<ImperativeValueHandler<Value>> {
                stored[Value::class] = value as? Any
                it(value)
            }

    }

    operator fun <D, R> ImperativeAction<D, R>.invoke(data: D) =
        call(this, data)

    operator fun <D, R> ImperativeAction<D, R>.invoke(callback: (D) -> R) {
        action(this, callback)
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
fun <R> delegate(ref: ImperativeActionDelegate.() -> R) =
    imperative(ImperativeActionDelegate(), ref)

@Scene2dCallbackDsl
fun delegate() =
    ImperativeActionDelegate()

@Scene2dCallbackDsl
fun <A : ImperativeActions, R> imperative(actions: A, ref: A.() -> R): ImperativeHandler<R, A> =
    ImperativeHandler(actions.ref(), actions)