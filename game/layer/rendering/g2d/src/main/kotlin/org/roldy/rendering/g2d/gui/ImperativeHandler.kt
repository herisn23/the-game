package org.roldy.rendering.g2d.gui

import org.roldy.core.cast
import kotlin.properties.Delegates

interface ImperativeActions

interface ImperativeAction

typealias ImperativeRunnableValue = () -> Unit

interface ImperativeFunction<Data, Return, A : ImperativeAction>

interface ImperativeValue<V, A : ImperativeAction>

typealias ImperativeValueHandler<Value> = (Value) -> Unit

typealias ImperativeActionHandler<Data, Return> = (Data) -> Return

typealias Value<Ref, V> = ImperativeHandler<Ref, ImperativeActionValue<V>>

typealias Delegate<Ref, A> = ImperativeHandler<Ref, ImperativeActionDelegate<A>>

@Scene2dCallbackDsl
class ImperativeActionDelegate<A : ImperativeAction> : ImperativeActions {

    val valueListeners =
        mutableMapOf<ImperativeValue<*, *>, MutableList<ImperativeValueHandler<*>>>()

    val functionListeners =
        mutableMapOf<ImperativeFunction<*, *, *>, MutableList<ImperativeActionHandler<*, *>>>()

    val stored: MutableMap<ImperativeValue<*, *>, Any?> = mutableMapOf()

    fun <Value, I> value(
        value: I,
        listener: ImperativeValueHandler<Value>
    ) where I : ImperativeValue<Value, A> {
        valueListeners.getOrPut(value) { mutableListOf() }.add(listener)
    }

    fun <D, R, F> function(
        action: F,
        listener: ImperativeActionHandler<D, R>
    ) where F : ImperativeFunction<D, R, A> {
        functionListeners.getOrPut(action) { mutableListOf() }.add(listener)
    }


    inline fun <reified Value, I> get(value: I) where I : ImperativeValue<Value, A> =
        stored[value].cast<Value>()

    fun <R, D, F> call(function: F, data: D) where F : ImperativeFunction<D, R, A> =
        functionListeners[function]?.forEach {
            it.cast<ImperativeActionHandler<D, R>>().invoke(data)
        }

    fun <Value, I> set(valueType: I, value: Value) where I : ImperativeValue<Value, A> {
        stored[valueType] = value as? Any
        valueListeners[valueType]?.forEach {
            it.cast<ImperativeValueHandler<Value>> {
                it(value)
            }
        }
    }

    @JvmName("getValue")
    inline fun <reified Value, I> I.get() where I : ImperativeValue<Value, A> =
        get(this)

    @JvmName("getCallableValue")
    inline operator fun <reified Value : ImperativeRunnableValue, I> I.invoke() where I : ImperativeValue<Value, A> =
        get(this).invoke()

    @JvmName("setValue")
    inline fun <reified Value, I> I.set(value: Value) where I : ImperativeValue<Value, A> =
        set(this, value)

    operator fun <Value, I> I.invoke(listener: ImperativeValueHandler<Value>) where I : ImperativeValue<Value, A> {
        value(this, listener)
    }

    operator fun <D, R, F> F.invoke(data: D) where F : ImperativeFunction<D, R, A> =
        call(this, data)

    operator fun <D, R, F> F.invoke(callback: (D) -> R) where F : ImperativeFunction<D, R, A> {
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
fun <R, A : ImperativeAction> delegate(
    ref: ImperativeActionDelegate<A>.() -> R
): ImperativeHandler<R, ImperativeActionDelegate<A>> =
    imperative(ImperativeActionDelegate(), ref)

@Scene2dCallbackDsl
fun <A : ImperativeActions, R> imperative(actions: A, ref: A.() -> R): ImperativeHandler<R, A> =
    ImperativeHandler(actions.ref(), actions)