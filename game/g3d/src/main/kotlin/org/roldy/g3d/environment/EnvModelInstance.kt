package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelInstance

class EnvModelInstance(
    val name: String,
    val instance: ModelInstance
) {


    fun model(camera: Camera) =
        instance

}

