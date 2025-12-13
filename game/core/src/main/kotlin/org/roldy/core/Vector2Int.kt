package org.roldy.core

import com.badlogic.gdx.math.Vector2
import kotlinx.serialization.Serializable

interface IVector2Int {
    val x: Int
    val y: Int
}

data class MutableVector2Int(override var x: Int, override var y: Int) : IVector2Int

@Serializable
data class Vector2Int(override val x: Int, override val y: Int) : IVector2Int, Comparable<Vector2Int> {
    val sum get() = x + y
    override fun compareTo(other: Vector2Int): Int =
        this.x.compareTo(other.x).takeIf { it != 0 } ?: this.y.compareTo(other.y)

}

fun Vector2Int.mutable() = MutableVector2Int(x, y)

infix operator fun Vector2Int.plus(add: Int): Vector2Int =
    Vector2Int(this.x + add, this.y + add)

infix operator fun Vector2Int.minus(dis: Int): Vector2Int =
    Vector2Int(this.x - dis, this.y - dis)

infix operator fun Vector2Int.div(div: Int): Vector2Int =
    Vector2Int(this.x / div, this.y / div)

infix operator fun Vector2Int.times(other: Vector2Int): Vector2Int =
    Vector2Int(this.x * other.x, this.y * other.y)

infix operator fun Vector2Int.plus(other: Vector2Int): Vector2Int =
    Vector2Int(this.x + other.x, this.y + other.y)

infix fun Int.x(other: Int) = Vector2Int(this, other)
infix fun Float.x(other: Float) = Vector2(this, other)

