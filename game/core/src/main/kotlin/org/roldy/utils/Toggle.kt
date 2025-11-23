package org.roldy.utils

import kotlin.properties.Delegates
import kotlin.reflect.KProperty


class Toggle(
    private val onTrue: () -> Unit = {},
    private val onFalse: () -> Unit = {}
) {
    fun interface Executable {
        fun execute()
    }

    private var state by Delegates.observable(false) { _, _, _ ->
        resolve(onTrue, onFalse)
    }
    val value get() = state

    operator fun invoke(onTrue: Executable, onFalse: Executable) {
        resolve(onTrue, onFalse)
        toggle()
    }

    private fun resolve(onTrue: Executable, onFalse: Executable) {
        if (state) {
            onTrue.execute()
        } else {
            onFalse.execute()
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
        state

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) = {
        state = value
    }

    operator fun invoke() {
        resolve(onTrue, onFalse)
        toggle()
    }

    private fun toggle() {
        state = !state
    }
}

fun toggle(
    onTrue: () -> Unit = {},
    onFalse: () -> Unit = {}
) = Toggle(onTrue, onFalse)