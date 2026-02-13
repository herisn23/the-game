package org.roldy.core.shader.uniform

import com.badlogic.gdx.graphics.glutils.ShaderProgram

class UniformSetter(
    private val program: ShaderProgram
) {
    fun EnvColorUniform.set(location: Int) {
        program.setUniformf(location, color.r, color.g, color.b)
    }

    fun BooleanUniform.set(location: Int) {
        program.setUniformi(location, int)
    }

    fun EnvTextureUniform.bind(location: Int, bind: Int) {
        texture.bind(bind)
        program.setUniformi(location, bind)
    }

    fun FloatValueUniform.set(location: Int) {
        program.setUniformf(location, value)
    }
}