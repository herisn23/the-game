package org.roldy.core.shader.attribute

import com.badlogic.gdx.graphics.glutils.ShaderProgram

class AttributesSetter(
    private val program: ShaderProgram
) {
    fun FoliageColorAttribute.set(location: Int) {
        program.setUniformf(location, color.r, color.g, color.b)
    }

    fun BooleanAttribute.set(location: Int) {
        program.setUniformi(location, asInt)
    }

    fun FoliageTextureAttribute.bind(location: Int, bind: Int) {
        texture.bind(bind)
        program.setUniformi(location, bind)
    }

    fun FloatValueAttribute.set(location: Int) {
        program.setUniformf(location, value)
    }
}