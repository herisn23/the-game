package org.roldy.data.tile

import org.roldy.core.Vector2Int
import org.roldy.core.x

/**
 * Hexagonal edge-to-edge directions for flat-top hexagons.
 */
enum class HexEdgeDirection {
    EAST,
    NORTHEAST,
    NORTHWEST,
    WEST,
    SOUTHWEST,
    SOUTHEAST;

    /**
     * Get offset for this direction based on stagger.
     * For Y-axis stagger (flat-top hexagons).
     */
    fun getOffset(isStaggered: Boolean): Vector2Int {
        return when (this) {
            EAST -> 1 x 0
            WEST -> -1 x 0
            NORTHEAST -> if (isStaggered) 0 x 1 else 1 x 1
            NORTHWEST -> if (isStaggered) -1 x 1 else 0 x 1
            SOUTHEAST -> if (isStaggered) 0 x -1 else 1 x -1
            SOUTHWEST -> if (isStaggered) -1 x -1 else 0 x -1
        }
    }
}

/**
 * Converts list of connected directions to 6-bit binary string.
 * Bit order per asset documentation: NORTHWEST, NORTHEAST, EAST, SOUTHEAST, SOUTHWEST, WEST
 *
 * Bit positions (right to left, 0-5):
 * Bit 0: WEST
 * Bit 1: SOUTHWEST
 * Bit 2: SOUTHEAST
 * Bit 3: EAST
 * Bit 4: NORTHEAST
 * Bit 5: NORTHWEST
 *
 * Examples:
 * - EAST + WEST = 001001
 * - WEST + EAST + SOUTHWEST = 001011
 */
fun connectionsToBitmask(connections: List<HexEdgeDirection>): String {
    var mask = 0

    connections.forEach { dir ->
        val bitPosition = when (dir) {
            HexEdgeDirection.WEST -> 0
            HexEdgeDirection.SOUTHWEST -> 1
            HexEdgeDirection.SOUTHEAST -> 2
            HexEdgeDirection.EAST -> 3
            HexEdgeDirection.NORTHEAST -> 4
            HexEdgeDirection.NORTHWEST -> 5
        }
        mask = mask or (1 shl bitPosition)
    }

    return mask.toString(2).padStart(6, '0')
}