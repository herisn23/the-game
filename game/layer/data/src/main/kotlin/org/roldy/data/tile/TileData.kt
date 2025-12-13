package org.roldy.data.tile

import org.roldy.core.Vector2Int

interface TileData {
    val walkCost: Float get() = 1f
    val coords: Vector2Int
}


fun List<TileData>?.walkCost(): Float {
    var sum = 1f
    this?.forEach {
        sum *= it.walkCost
    }
    return sum
}