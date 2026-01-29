package org.roldy.core.utils

import com.badlogic.gdx.graphics.glutils.ShaderProgram


operator fun ShaderProgram.invoke(name: String, set: ShaderProgram.(String) -> Unit) {
    if (hasUniform(name)) {
        set(name)
    }
}