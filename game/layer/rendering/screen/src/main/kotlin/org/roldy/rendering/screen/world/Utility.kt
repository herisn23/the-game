package org.roldy.rendering.screen.world

import kotlin.math.sqrt


fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
    val dx = (x1 - x2).toFloat()
    val dy = (y1 - y2).toFloat()
    return sqrt(dx * dx + dy * dy)
}