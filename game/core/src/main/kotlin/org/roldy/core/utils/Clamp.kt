package org.roldy.utils

fun Int.clamp(min: Int, max: Int): Int {
    return when {
        this >= max -> min
        this < min -> max - 1
        else -> this
    }
}