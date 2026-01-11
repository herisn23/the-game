package org.roldy.core.utils

import org.roldy.core.Vector2Int
import kotlin.math.abs

/**
 * Generates all tile coords from center within radius
 */
fun hexRadius(center: Vector2Int, radius: Int, min: Vector2Int, max: Vector2Int): List<Vector2Int> {
    val tiles = mutableListOf<Vector2Int>()
    for (x in center.x - radius..center.x + radius) {
        for (y in center.y - radius..center.y + radius) {
            val position = Vector2Int(x, y)
            if (
                hexDistance(center, position) <= radius &&
                position in min..max
            ) {
                tiles.add(position)
            }
        }
    }

    return tiles
}

/**
 * Calculate hexagonal distance using offset coordinates
 */
fun hexDistance(a: Vector2Int, b: Vector2Int): Int {
    // Convert offset coordinates to axial
    val ac = offsetToAxial(a)
    val bc = offsetToAxial(b)

    val dq = ac.x - bc.x
    val dr = ac.y - bc.y
    return (abs(dq) + abs(dr) + abs(dq + dr)) / 2
}

/**
 * Convert odd-r offset coordinates to axial coordinates
 */
private fun offsetToAxial(offset: Vector2Int): Vector2Int {
    val q = offset.x - (offset.y - (offset.y and 1)) / 2
    val r = offset.y
    return Vector2Int(q, r)
}

/**
 * Check if position is in radius
 */
fun inHexRadius(position: Vector2Int, radius: List<Vector2Int>): Boolean {
    return radius.contains(position)
}