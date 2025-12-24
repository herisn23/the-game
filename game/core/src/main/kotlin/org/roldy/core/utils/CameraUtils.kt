package org.roldy.core.utils

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3


fun Camera.unproject(vec: Vector2): Vector2 {
    val projection = Vector3(vec.x, vec.y, 0f)
    unproject(projection)
    vec.x = projection.x
    vec.y = projection.y
    return vec
}

fun Camera.project(vec: Vector2): Vector2 {
    val projection = Vector3(vec.x, vec.y, 0f)
    project(projection)
    vec.x = projection.x
    vec.y = projection.y
    return vec
}