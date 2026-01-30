package org.roldy.core.utils

import com.badlogic.gdx.graphics.glutils.ShaderProgram


operator fun ShaderProgram.invoke(name: String, set: ShaderProgram.(Int) -> Unit) {
    val uniformLocation = getUniformLocation(name)
    if (uniformLocation > -1) {
        set(uniformLocation)
    }
}