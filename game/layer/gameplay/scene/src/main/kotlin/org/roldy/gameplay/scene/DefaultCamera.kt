package org.roldy.gameplay.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera


fun camera(zoom: Float = 1f) =
    OrthographicCamera().apply {
        this.zoom = zoom
        position.set(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2, 1f)
    }