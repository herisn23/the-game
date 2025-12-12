package org.roldy.core

import com.badlogic.gdx.math.Vector2

interface IVector2Int {
    val x: Int
    val y: Int
}

data class Vector2Int(override val x: Int, override val y: Int) : IVector2Int {
    val sum get() = x + y
}

data class MutableVector2Int(override var x: Int, override var y: Int) : IVector2Int

infix fun Int.x(other: Int) = Vector2Int(this, other)
infix fun Float.x(other: Float) = Vector2(this, other)
infix operator fun Vector2Int.plus(other: Vector2Int): Vector2Int  =
    Vector2Int(this.x + other.x, this.y + other.y)

infix operator fun Vector2Int.times(other: Vector2Int): Vector2Int  =
    Vector2Int(this.x * other.x, this.y * other.y)
