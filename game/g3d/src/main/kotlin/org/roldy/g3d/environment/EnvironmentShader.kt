package org.roldy.g3d.environment

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.shader.ShaderAdapter
import org.roldy.core.shader.shaderProvider

class EnvironmentShader(
    offsetProvider: OffsetProvider,
    renderable: Renderable,
    config: Config
) : ShaderAdapter(renderable, config, offsetProvider) {
    var diffuseColor = floatArrayOf(1f, 1f, 1f, 1f)
    var emissiveColor = floatArrayOf(0f, 0f, 0f, 1f)

    override fun render(renderable: Renderable) {

        program.setUniform4fv(uDiffuseColor, diffuseColor, 0, 4)
        program.setUniform4fv(uEmissiveColor, emissiveColor, 0, 4)

        shift()
        setLightColor()
        setLightDir()
        setAmbientLightColor()

        diffuseTexture.bind(0)
        emissiveTexture.bind(1)

        // Rebind diffuse to unit 0 (important!)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)

        super.render(renderable)
    }
}

fun environmentShaderProvider(offsetProvider: OffsetProvider) =
    shaderProvider {
        EnvironmentShader(offsetProvider, it, DefaultShader.Config().apply {
            vertexShader = envGenericVert
            fragmentShader = envGenericFrag
        })
    }