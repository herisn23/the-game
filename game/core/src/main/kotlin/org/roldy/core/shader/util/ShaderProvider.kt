package org.roldy.core.shader.util

import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
import org.roldy.core.asset.ShaderLoader


fun shaderProvider(
    create: ShaderLoader.(Renderable) -> Shader
) = object : BaseShaderProvider() {
    override fun createShader(renderable: Renderable): Shader =
        ShaderLoader.create(renderable)
}