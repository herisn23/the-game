package org.roldy.core.utils

import org.roldy.core.Vector2Int
import kotlin.math.abs


/**
 * Calculates hex distance between two coordinates.
 */
fun hexDistance2(a: Vector2Int, b: Vector2Int): Float {
    return (abs(a.x - b.x) + abs(a.y - b.y)).toFloat()
}