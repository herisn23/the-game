package org.roldy.g3d.pawn

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider

class PawnShader(
    val configuration: PawnConfiguration,
    renderable: Renderable
) : DefaultShader(renderable, Config().apply {
    fragmentShader = Gdx.files.internal("shaders/character.fragment.glsl").readString()
}) {
    private val maskTextures = ArmorMaskTextures()

    override fun render(renderable: Renderable) {
        colorize(renderable)
        super.render(renderable)
    }

    fun colorize(renderable: Renderable) {
        val node =
            configuration.instance.meshMap.getValue(with(configuration.instance) { renderable.meshPart.mappingId() })

        configuration.let { pawn ->
            val config = pawn.getShaderConfig(node)
            // Base texture - unit 0
            val diffuse = renderable.material.get(TextureAttribute::class.java, TextureAttribute.Diffuse)
            diffuse?.textureDescription?.texture?.bind(0)

            maskTextures.texture2.bind(0)
            program.setUniformi("u_texture2", 0)


            // Bind all mask textures
            maskTextures.texture0.bind(1)
            program.setUniformi("u_texture0", 1)

            maskTextures.texture1.bind(2)
            program.setUniformi("u_texture1", 2)

            maskTextures.texture6.bind(3)
            program.setUniformi("u_texture6", 3)

            maskTextures.texture3.bind(4)
            program.setUniformi("u_texture3", 4)

            maskTextures.texture5.bind(5)
            program.setUniformi("u_texture5", 5)

            maskTextures.texture4.bind(6)
            program.setUniformi("u_texture4", 6)

            maskTextures.texture7.bind(7)
            program.setUniformi("u_texture7", 7)

            // Set all color uniforms
            setColorUniform("u_skinColor", config.skinColor)
            setColorUniform("u_eyesColor", config.eyesColor)
            setColorUniform("u_hairColor", config.hairColor)
            setColorUniform("u_scleraColor", config.scleraColor)
            setColorUniform("u_lipsColor", config.lipsColor)
            setColorUniform("u_scarsColor", config.scarsColor)
            setColorUniform("u_metal1Color", config.metal1Color)
            setColorUniform("u_metal2Color", config.metal2Color)
            setColorUniform("u_metal3Color", config.metal3Color)
            setColorUniform("u_leather1Color", config.leather1Color)
            setColorUniform("u_leather2Color", config.leather2Color)
            setColorUniform("u_leather3Color", config.leather3Color)
            setColorUniform("u_cloth1Color", config.cloth1Color)
            setColorUniform("u_cloth2Color", config.cloth2Color)
            setColorUniform("u_cloth3Color", config.cloth3Color)
            setColorUniform("u_gems1Color", config.gems1Color)
            setColorUniform("u_gems2Color", config.gems2Color)
            setColorUniform("u_gems3Color", config.gems3Color)
            setColorUniform("u_feathers1Color", config.feathers1Color)
            setColorUniform("u_feathers2Color", config.feathers2Color)
            setColorUniform("u_feathers3Color", config.feathers3Color)

            // Set smoothness uniforms
            program.setUniformf("u_skinSmoothness", config.skinSmoothness)
            program.setUniformf("u_eyesSmoothness", config.eyesSmoothness)
            program.setUniformf("u_hairSmoothness", config.hairSmoothness)
            program.setUniformf("u_scleraSmoothness", config.scleraSmoothness)
            program.setUniformf("u_lipsSmoothness", config.lipsSmoothness)
            program.setUniformf("u_scarsSmoothness", config.scarsSmoothness)
            program.setUniformf("u_metal1Smoothness", config.metal1Smoothness)
            program.setUniformf("u_metal2Smoothness", config.metal2Smoothness)
            program.setUniformf("u_metal3Smoothness", config.metal3Smoothness)
            program.setUniformf("u_leather1Smoothness", config.leather1Smoothness)
            program.setUniformf("u_leather2Smoothness", config.leather2Smoothness)
            program.setUniformf("u_leather3Smoothness", config.leather3Smoothness)
            program.setUniformf("u_gems1Smoothness", config.gems1Smoothness)
            program.setUniformf("u_gems2Smoothness", config.gems2Smoothness)
            program.setUniformf("u_gems3Smoothness", config.gems3Smoothness)

            // Set metallic uniforms
            program.setUniformf("u_metal1Metallic", config.metal1Metallic)
            program.setUniformf("u_metal2Metallic", config.metal2Metallic)
            program.setUniformf("u_metal3Metallic", config.metal3Metallic)
        }

        // Set lighting uniforms
        program.setUniformf("u_ambientLight", 0.4f, 0.4f, 0.4f)
        program.setUniformf("u_dirLightColor", 0.8f, 0.8f, 0.8f)
        program.setUniformf("u_dirLightDir", -1f, -0.8f, -0.2f)

        //update lighting
        // Extract lighting from Environment
        val env = renderable.environment

        // Get ambient light
        val ambientLight = env?.get(ColorAttribute::class.java, ColorAttribute.AmbientLight) as? ColorAttribute
        if (ambientLight != null) {
            program.setUniformf(
                "u_ambientLight",
                ambientLight.color.r,
                ambientLight.color.g,
                ambientLight.color.b
            )
        } else {
            program.setUniformf("u_ambientLight", 0.4f, 0.4f, 0.4f)
        }

        // Get directional light (first one)
        if (env != null && env.has(DirectionalLightsAttribute.Type)) {
            val dirLights = env.get(
                DirectionalLightsAttribute::class.java,
                DirectionalLightsAttribute.Type
            ) as DirectionalLightsAttribute
            if (dirLights.lights.size > 0) {
                val light = dirLights.lights.first()
                program.setUniformf(
                    "u_dirLightColor",
                    light.color.r,
                    light.color.g,
                    light.color.b
                )
                program.setUniformf(
                    "u_dirLightDir",
                    light.direction.x,
                    light.direction.y,
                    light.direction.z
                )
            }
        } else {
            // Fallback
            program.setUniformf("u_dirLightColor", 0.8f, 0.8f, 0.8f)
            program.setUniformf("u_dirLightDir", -1f, -0.8f, -0.2f)
        }
    }

    private fun setColorUniform(name: String, color: Color) {
        program.setUniformf(name, color.r, color.g, color.b)
    }

    private class ArmorMaskTextures {
        val texture0: Texture = PawnAssetManager.mask0.get().let(::setupTexture)
        val texture1: Texture = PawnAssetManager.mask1.get().let(::setupTexture)
        val texture2: Texture = PawnAssetManager.mask2.get().let(::setupTexture)
        val texture3: Texture = PawnAssetManager.mask3.get().let(::setupTexture)
        val texture4: Texture = PawnAssetManager.mask4.get().let(::setupTexture)
        val texture5: Texture = PawnAssetManager.mask5.get().let(::setupTexture)
        val texture6: Texture = PawnAssetManager.mask6.get().let(::setupTexture)
        val texture7: Texture = PawnAssetManager.mask7.get().let(::setupTexture)

        fun setupTexture(tex: Texture): Texture {
            // Flip texture vertically
            if (!tex.textureData.isPrepared) {
                tex.textureData.prepare()
            }
            val pixmap = tex.textureData.consumePixmap()

            // Flip Y
            val flipped = Pixmap(pixmap.width, pixmap.height, pixmap.format)
            for (y in 0 until pixmap.height) {
                for (x in 0 until pixmap.width) {
                    flipped.drawPixel(x, pixmap.height - 1 - y, pixmap.getPixel(x, y))
                }
            }

            val flippedTex = Texture(flipped)
            flippedTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            flippedTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

            pixmap.dispose()
            flipped.dispose()
            return flippedTex
        }
    }
}

class PawnShaderProvider(
    private val configuration: PawnConfiguration,
) : BaseShaderProvider() {

    override fun createShader(renderable: Renderable): Shader {
        return PawnShader(configuration, renderable)
    }
}

interface ShaderConfig {
    val skinColor: Color
    val eyesColor: Color
    val hairColor: Color
    val scleraColor: Color
    val lipsColor: Color
    val scarsColor: Color

    // Metal colors
    val metal1Color: Color
    val metal2Color: Color
    val metal3Color: Color

    // Leather colors
    val leather1Color: Color
    val leather2Color: Color
    val leather3Color: Color

    // Cloth colors
    val cloth1Color: Color
    val cloth2Color: Color
    val cloth3Color: Color

    // Gems colors
    val gems1Color: Color
    val gems2Color: Color
    val gems3Color: Color

    // Feathers colors
    val feathers1Color: Color
    val feathers2Color: Color
    val feathers3Color: Color


    // Smoothness values
    val skinSmoothness: Float
    val eyesSmoothness: Float
    val hairSmoothness: Float
    val scleraSmoothness: Float
    val lipsSmoothness: Float
    val scarsSmoothness: Float
    val metal1Smoothness: Float
    val metal2Smoothness: Float
    val metal3Smoothness: Float
    val leather1Smoothness: Float
    val leather2Smoothness: Float
    val leather3Smoothness: Float
    val gems1Smoothness: Float
    val gems2Smoothness: Float
    val gems3Smoothness: Float

    // Metallic values
    val metal1Metallic: Float
    val metal2Metallic: Float
    val metal3Metallic: Float
}

class DefaultShaderConfig : ShaderConfig {
    override var skinColor = Color(2.02193f, 1.0081f, 0.6199315f, 1f)
    override var eyesColor = Color(0.0734529f, 0.1320755f, 0.05046281f, 1f)
    override var hairColor = Color(0.5943396f, 0.3518379f, 0.1093361f, 1f)
    override var scleraColor = Color(0.9056604f, 0.8159487f, 0.8159487f, 1f)
    override var lipsColor = Color(0.8301887f, 0.3185886f, 0.2780349f, 1f)
    override var scarsColor = Color(0.8490566f, 0.5037117f, 0.3884835f, 1f)

    // Metal colors
    override var metal1Color = Color(2f, 0.682353f, 0.1960784f, 1f)
    override var metal2Color = Color(0.4674706f, 0.4677705f, 0.5188679f, 1f)
    override var metal3Color = Color(0.4383232f, 0.4383232f, 0.4716981f, 1f)

    // Leather colors
    override var leather1Color = Color(0.4811321f, 0.2041155f, 0.08851016f, 1f)
    override var leather2Color = Color(0.4245283f, 0.190437f, 0.09011215f, 1f)
    override var leather3Color = Color(0.1698113f, 0.04637412f, 0.02963688f, 1f)//.mul(2.5f)

    // Cloth colors
    override var cloth1Color = Color(0.1465379f, 0.282117f, 0.3490566f, 1f)
    override var cloth2Color = Color(1f, 0f, 0f, 1f)
    override var cloth3Color = Color(0.8773585f, 0.6337318f, 0.3434941f, 1f)

    // Gems colors
    override var gems1Color = Color(0.3773585f, 0f, 0.06650025f, 1f)
    override var gems2Color = Color(0.2023368f, 0f, 0.4339623f, 1f)
    override var gems3Color = Color(0f, 0.1132075f, 0.01206957f, 1f)

    // Feathers colors
    override var feathers1Color = Color(0.7735849f, 0.492613f, 0.492613f, 1f)
    override var feathers2Color = Color(0.6792453f, 0f, 0f, 1f)
    override var feathers3Color = Color(0f, 0.1793142f, 0.7264151f, 1f)


    // Smoothness values
    override var skinSmoothness = 0.3f
    override var eyesSmoothness = 0.7f
    override var hairSmoothness = 0.1f
    override var scleraSmoothness = 0.5f
    override var lipsSmoothness = 0.4f
    override var scarsSmoothness = 0.3f
    override var metal1Smoothness = 0.7f
    override var metal2Smoothness = 0.7f
    override var metal3Smoothness = 0.7f
    override var leather1Smoothness = 0.3f
    override var leather2Smoothness = 0.3f
    override var leather3Smoothness = 0.3f
    override var gems1Smoothness = 1.0f
    override var gems2Smoothness = 0.0f
    override var gems3Smoothness = 0.0f

    // Metallic values
    override var metal1Metallic = 0.65f
    override var metal2Metallic = 0.65f
    override var metal3Metallic = 0.65f
}