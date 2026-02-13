package org.roldy.core.shader

import com.badlogic.gdx.graphics.g3d.Attribute
import org.roldy.core.shader.uniform.UniformValue

class UniformAttribute(
    type: Long,
    var uniforms: Map<String, UniformValue>
) : Attribute(type) {

    companion object {
        val uniformsAttr = register("uniformsAttr")

        fun create(uniforms: Map<String, UniformValue>) =
            UniformAttribute(uniformsAttr, uniforms)
    }

    override fun copy(): Attribute =
        UniformAttribute(type, uniforms)

    override fun compareTo(other: Attribute?): Int = 0
}