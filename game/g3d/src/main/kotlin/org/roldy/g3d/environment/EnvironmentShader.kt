package org.roldy.g3d.environment

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider

class EnvironmentShader(
    private val offsetProvider: OffsetProvider,
    renderable: Renderable,
    config: Config
) : DefaultShader(renderable, config) {
    private var uDiffuseColor = 0
    private var uEmissiveColor = 0
    private var uDiffuseTexture = 0
    private var uEmissiveTexture = 0
    private var uLightDir = 0
    private var uLightColor = 0
    private var uAmbientLight = 0
    private var uRenderOffset = 0

    var diffuseColor = floatArrayOf(1f, 1f, 1f, 1f)
    var emissiveColor = floatArrayOf(0f, 0f, 0f, 1f)
    var diffuseTexture: Texture =
        renderable.material.get(TextureAttribute::class.java, TextureAttribute.Diffuse).textureDescription.texture
    var emissiveTexture: Texture =
        renderable.material.get(TextureAttribute::class.java, TextureAttribute.Emissive).textureDescription.texture

    val ambientLight = renderable.environment.get(ColorAttribute::class.java, ColorAttribute.AmbientLight)
    val light = renderable.environment.run {
        get(
            DirectionalLightsAttribute::class.java,
            DirectionalLightsAttribute.Type
        ) as DirectionalLightsAttribute
    }.lights.first()

    override fun init() {
        super.init()
        uDiffuseColor = program.fetchUniformLocation("u_diffuseColor", false)
        uEmissiveTexture = program.fetchUniformLocation("u_diffuseTexture", false)
        uEmissiveColor = program.fetchUniformLocation("u_emissiveColor", false)
        uEmissiveTexture = program.fetchUniformLocation("u_emissiveTexture", false)
        uLightDir = program.fetchUniformLocation("u_lightDir", false)
        uLightColor = program.fetchUniformLocation("u_lightColor", false)
        uAmbientLight = program.fetchUniformLocation("u_ambientLight", false)
        uRenderOffset = program.fetchUniformLocation("u_renderOffset", false)
    }

    override fun render(renderable: Renderable) {
        program.setUniform4fv(uDiffuseColor, diffuseColor, 0, 4)
        program.setUniform4fv(uEmissiveColor, emissiveColor, 0, 4)
        val originOffset = offsetProvider.shiftOffset
        program.setUniformf(uRenderOffset, originOffset.x, originOffset.y, originOffset.z)

        program.setUniformf(
            uLightColor,
            light.color.r,
            light.color.g,
            light.color.b
        )
        program.setUniformf(
            uLightDir,
            light.direction.x,
            light.direction.y,
            light.direction.z
        )
        program.setUniformf(
            uAmbientLight,
            ambientLight.color.r,
            ambientLight.color.g,
            ambientLight.color.b
        )

        diffuseTexture.bind(0)
        program.setUniformi(uDiffuseTexture, 0)

        emissiveTexture.bind(1)
        program.setUniformi(uEmissiveTexture, 1)

        // Rebind diffuse to unit 0 (important!)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)

        super.render(renderable)
    }
}

fun environmentShaderProvider(offsetProvider: OffsetProvider) =
    object : DefaultShaderProvider() {
        override fun createShader(renderable: Renderable): Shader =
            EnvironmentShader(offsetProvider, renderable, DefaultShader.Config().apply {
                vertexShader = ShaderLoader.envGenericVert
                fragmentShader = ShaderLoader.envGenericFrag
            })
    }