package org.roldy.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera


fun camera(zoom: Float = 1f) =
    OrthographicCamera().apply {
        this.zoom = zoom
        position.set(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2, 1f)
    }

fun camera3D() =
    PerspectiveCamera(
        67f,
        Gdx.graphics.backBufferWidth.toFloat(),
        Gdx.graphics.backBufferHeight.toFloat()
    ).apply {
        position.set(0f, 0f, 0f)
        lookAt(0f, 0f, 0f)
        near = 1f
        far = 20000f
        update()
    }