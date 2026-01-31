package org.roldy.core.utils


fun repeat(xRange: IntRange, yRange: IntRange, action: (Int, Int) -> Unit) {
    for (x in xRange) {
        for (y in yRange) {
            action(x, y)
        }
    }
}

fun repeat(x: Int, y: Int, action: (Int, Int) -> Unit) {
    repeat(0..x, 0..y, action)
}