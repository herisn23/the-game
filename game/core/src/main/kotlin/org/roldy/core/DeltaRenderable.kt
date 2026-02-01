package org.roldy.core

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Environment

interface DeltaRenderable {

    context(delta: Float)
    fun render()
}

interface EnvironmentalRenderable {
    context(delta: Float, environment: Environment, camera: Camera)
    fun render()
}