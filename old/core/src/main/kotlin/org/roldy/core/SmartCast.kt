package org.roldy.core


inline fun <reified T> Any?.cast(callback: (T) -> Unit) {
    if (this is T) {
        callback(this)
    }
}

inline fun <reified T> Any?.cast(): T {
    if (this is T) {
        return this
    } else {
        error("Unexpected cast: $this")
    }
}