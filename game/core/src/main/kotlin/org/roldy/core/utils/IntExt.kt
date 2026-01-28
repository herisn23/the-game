package org.roldy.core.utils

fun Int.cycle(range: IntRange): Int {
    return cycle(range.first, range.last)
}

fun Int.cycle(min: Int, max: Int): Int {
    return when {
        this > max -> min
        this < min -> max
        else -> this
    }
}