package org.roldy.terrain.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram


fun loadShader(vertPath: String, fragPath: String): ShaderProgram {
    val vertexShader = Gdx.files.internal(vertPath).readString()
    val fragmentShader = Gdx.files.internal(fragPath).readString()

    val shader = ShaderProgram(vertexShader, fragmentShader)

    if (!shader.isCompiled) {
        Gdx.app.error("Shader", "Failed to compile shader:")
        Gdx.app.error("Shader", shader.log)
    }

    return shader
}

val terrainShader by lazy {
    loadShader("shader/vert.glsl", "shader/frag.glsl")
}