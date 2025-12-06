package org.roldy.scene.world.populator

import com.badlogic.gdx.math.Vector2
import org.roldy.core.Vector2Int
import org.roldy.core.x
import org.roldy.scene.world.chunk.WorldMapChunk
import kotlin.math.sqrt


fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
    val dx = (x1 - x2).toFloat()
    val dy = (y1 - y2).toFloat()
    return sqrt(dx * dx + dy * dy)
}

fun WorldMapChunk.objectPosition(coords: Vector2Int): Vector2 {
    val x = coords.x * tileSize + (tileSize / 2).toFloat()
    val y = coords.y * tileSize + (tileSize / 2).toFloat()
    return x x y
}