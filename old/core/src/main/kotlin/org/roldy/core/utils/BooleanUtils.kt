package org.roldy.core.utils

infix fun Boolean.nor(other: Boolean): Boolean = !(this || other)


class Take<T> {
    var onNull: () -> Unit = {}
    var onNotNull: (T) -> Unit = {}
}

@JvmName("takeExtension")
fun <T> T?.take(bind: Take<T>.() -> Unit) =
    take(this, bind)

fun <T> take(value: T?, bind: Take<T>.() -> Unit) =
    Take<T>().apply {
        bind()
        if (value == null) {
            onNull()
        } else {
            onNotNull(value)
        }
    }