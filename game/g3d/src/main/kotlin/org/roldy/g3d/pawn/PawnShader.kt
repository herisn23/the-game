package org.roldy.g3d.pawn

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
import org.roldy.core.ColorHDR
import org.roldy.core.asset.ShaderLoader

object PawnShaderUniforms {
    const val INTENSITY = "Intensity"

    interface ColorUniform {
        val base: String
        val intensity: String
    }

    object SkinColor : ColorUniform {
        override val base: String = "u_skinColor"
        override val intensity: String = "$base${INTENSITY}"
    }

    object EyesColor : ColorUniform {
        override val base: String = "u_eyesColor"
        override val intensity: String = "$base${INTENSITY}"
    }

    object HairColor : ColorUniform {
        override val base: String = "u_hairColor"
        override val intensity: String = "$base${INTENSITY}"
    }

    object ScleraColor : ColorUniform {
        override val base: String = "u_scleraColor"
        override val intensity: String = "$base${INTENSITY}"
    }

    object LipsColor : ColorUniform {
        override val base: String = "u_lipsColor"
        override val intensity: String = "$base${INTENSITY}"
    }

    object ScarsColor : ColorUniform {
        override val base: String = "u_scarsColor"
        override val intensity: String = "$base${INTENSITY}"
    }

    // Metal colors
    object Metal1Color : ColorUniform {
        override val base: String = "u_metal1Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Metal2Color : ColorUniform {
        override val base: String = "u_metal2Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Metal3Color : ColorUniform {
        override val base: String = "u_metal3Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    // Leather colors
    object Leather1Color : ColorUniform {
        override val base: String = "u_leather1Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Leather2Color : ColorUniform {
        override val base: String = "u_leather2Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Leather3Color : ColorUniform {
        override val base: String = "u_leather3Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    // Cloth colors
    object Cloth1Color : ColorUniform {
        override val base: String = "u_cloth1Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Cloth2Color : ColorUniform {
        override val base: String = "u_cloth2Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Cloth3Color : ColorUniform {
        override val base: String = "u_cloth3Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    // Gems colors
    object Gem1Color : ColorUniform {
        override val base: String = "u_gems1Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Gem2Color : ColorUniform {
        override val base: String = "u_gems2Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Gem3Color : ColorUniform {
        override val base: String = "u_gems3Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    // Feathers colors
    object Feather1Color : ColorUniform {
        override val base: String = "u_feathers1Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Feather2Color : ColorUniform {
        override val base: String = "u_feathers2Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    object Feather3Color : ColorUniform {
        override val base: String = "u_feathers3Color"
        override val intensity: String = "$base${INTENSITY}"
    }

    // textures
    const val U_TEXTURE0 = "u_texture0"
    const val U_TEXTURE1 = "u_texture1"
    const val U_TEXTURE2 = "u_texture2"
    const val U_TEXTURE3 = "u_texture3"
    const val U_TEXTURE4 = "u_texture4"
    const val U_TEXTURE5 = "u_texture5"
    const val U_TEXTURE6 = "u_texture6"
    const val U_TEXTURE7 = "u_texture7"

    const val U_SKIN_SMOOTHNESS = "u_skinSmoothness"
    const val U_EYES_SMOOTHNESS = "u_eyesSmoothness"
    const val U_HAIR_SMOOTHNESS = "u_hairSmoothness"
    const val U_SCLERA_SMOOTHNESS = "u_scleraSmoothness"
    const val U_LIPS_SMOOTHNESS = "u_lipsSmoothness"
    const val U_SCARS_SMOOTHNESS = "u_scarsSmoothness"
    const val U_METAL1_SMOOTHNESS = "u_metal1Smoothness"
    const val U_METAL2_SMOOTHNESS = "u_metal2Smoothness"
    const val U_METAL3_SMOOTHNESS = "u_metal3Smoothness"
    const val U_LEATHER1_SMOOTHNESS = "u_leather1Smoothness"
    const val U_LEATHER2_SMOOTHNESS = "u_leather2Smoothness"
    const val U_LEATHER3_SMOOTHNESS = "u_leather3Smoothness"
    const val U_GEMS1_SMOOTHNESS = "u_gems1Smoothness"
    const val U_GEMS2_SMOOTHNESS = "u_gems2Smoothness"
    const val U_GEMS3_SMOOTHNESS = "u_gems3Smoothness"

    const val U_METAL1_METALLIC = "u_metal1Metallic"
    const val U_METAL2_METALLIC = "u_metal2Metallic"
    const val U_METAL3_METALLIC = "u_metal3Metallic"
    const val U_AMBIENT_LIGHT = "u_ambientLight"
    const val U_DIR_LIGHT_COLOR = "u_dirLightColor"
    const val U_DIR_LIGHT_DIR = "u_dirLightDir"

}

class PawnShader(
    val configuration: PawnManager,
    renderable: Renderable
) : DefaultShader(renderable, Config().apply {
    fragmentShader = ShaderLoader.characterFrag
}) {
    private val maskTextures = ArmorMaskTextures()

    override fun end() {
        // Reset texture unit after terrain rendering
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
    }

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
            program.setUniformi(PawnShaderUniforms.U_TEXTURE2, 0)


            // Bind all mask textures
            maskTextures.texture0.bind(1)
            program.setUniformi(PawnShaderUniforms.U_TEXTURE0, 1)

            maskTextures.texture1.bind(2)
            program.setUniformi(PawnShaderUniforms.U_TEXTURE1, 2)

            maskTextures.texture6.bind(3)
            program.setUniformi(PawnShaderUniforms.U_TEXTURE6, 3)

            maskTextures.texture3.bind(4)
            program.setUniformi(PawnShaderUniforms.U_TEXTURE3, 4)

            maskTextures.texture5.bind(5)
            program.setUniformi(PawnShaderUniforms.U_TEXTURE5, 5)

            maskTextures.texture4.bind(6)
            program.setUniformi(PawnShaderUniforms.U_TEXTURE4, 6)

            maskTextures.texture7.bind(7)
            program.setUniformi(PawnShaderUniforms.U_TEXTURE7, 7)

            // Set all color uniforms
            setHDRColorUniform(PawnShaderUniforms.SkinColor, config.skinColor)
            setHDRColorUniform(PawnShaderUniforms.EyesColor, config.eyesColor)
            setHDRColorUniform(PawnShaderUniforms.HairColor, config.hairColor)
            setHDRColorUniform(PawnShaderUniforms.ScleraColor, config.scleraColor)
            setHDRColorUniform(PawnShaderUniforms.LipsColor, config.lipsColor)
            setHDRColorUniform(PawnShaderUniforms.ScarsColor, config.scarsColor)
            setHDRColorUniform(PawnShaderUniforms.Metal1Color, config.metal1Color)
            setHDRColorUniform(PawnShaderUniforms.Metal2Color, config.metal2Color)
            setHDRColorUniform(PawnShaderUniforms.Metal3Color, config.metal3Color)
            setHDRColorUniform(PawnShaderUniforms.Leather1Color, config.leather1Color)
            setHDRColorUniform(PawnShaderUniforms.Leather2Color, config.leather2Color)
            setHDRColorUniform(PawnShaderUniforms.Leather3Color, config.leather3Color)
            setHDRColorUniform(PawnShaderUniforms.Cloth1Color, config.cloth1Color)
            setHDRColorUniform(PawnShaderUniforms.Cloth2Color, config.cloth2Color)
            setHDRColorUniform(PawnShaderUniforms.Cloth3Color, config.cloth3Color)
            setHDRColorUniform(PawnShaderUniforms.Gem1Color, config.gems1Color)
            setHDRColorUniform(PawnShaderUniforms.Gem2Color, config.gems2Color)
            setHDRColorUniform(PawnShaderUniforms.Gem3Color, config.gems3Color)
            setHDRColorUniform(PawnShaderUniforms.Feather1Color, config.feathers1Color)
            setHDRColorUniform(PawnShaderUniforms.Feather2Color, config.feathers2Color)
            setHDRColorUniform(PawnShaderUniforms.Feather3Color, config.feathers3Color)

            // Set smoothness uniforms
            program.setUniformf(PawnShaderUniforms.U_SKIN_SMOOTHNESS, config.skinSmoothness)
            program.setUniformf(PawnShaderUniforms.U_EYES_SMOOTHNESS, config.eyesSmoothness)
            program.setUniformf(PawnShaderUniforms.U_HAIR_SMOOTHNESS, config.hairSmoothness)
            program.setUniformf(PawnShaderUniforms.U_SCLERA_SMOOTHNESS, config.scleraSmoothness)
            program.setUniformf(PawnShaderUniforms.U_LIPS_SMOOTHNESS, config.lipsSmoothness)
            program.setUniformf(PawnShaderUniforms.U_SCARS_SMOOTHNESS, config.scarsSmoothness)
            program.setUniformf(PawnShaderUniforms.U_METAL1_SMOOTHNESS, config.metal1Smoothness)
            program.setUniformf(PawnShaderUniforms.U_METAL2_SMOOTHNESS, config.metal2Smoothness)
            program.setUniformf(PawnShaderUniforms.U_METAL3_SMOOTHNESS, config.metal3Smoothness)
            program.setUniformf(PawnShaderUniforms.U_LEATHER1_SMOOTHNESS, config.leather1Smoothness)
            program.setUniformf(PawnShaderUniforms.U_LEATHER2_SMOOTHNESS, config.leather2Smoothness)
            program.setUniformf(PawnShaderUniforms.U_LEATHER3_SMOOTHNESS, config.leather3Smoothness)
            program.setUniformf(PawnShaderUniforms.U_GEMS1_SMOOTHNESS, config.gems1Smoothness)
            program.setUniformf(PawnShaderUniforms.U_GEMS2_SMOOTHNESS, config.gems2Smoothness)
            program.setUniformf(PawnShaderUniforms.U_GEMS3_SMOOTHNESS, config.gems3Smoothness)

            // Set metallic uniforms
            program.setUniformf(PawnShaderUniforms.U_METAL1_METALLIC, config.metal1Metallic)
            program.setUniformf(PawnShaderUniforms.U_METAL2_METALLIC, config.metal2Metallic)
            program.setUniformf(PawnShaderUniforms.U_METAL3_METALLIC, config.metal3Metallic)
        }

        // Set lighting uniforms
        program.setUniformf(PawnShaderUniforms.U_AMBIENT_LIGHT, 0.4f, 0.4f, 0.4f)
        program.setUniformf(PawnShaderUniforms.U_DIR_LIGHT_COLOR, 0.8f, 0.8f, 0.8f)
        program.setUniformf(PawnShaderUniforms.U_DIR_LIGHT_DIR, -1f, -0.8f, -0.2f)

        //update lighting
        // Extract lighting from Environment
        val env = renderable.environment

        // Get ambient light
        val ambientLight = env?.get(ColorAttribute::class.java, ColorAttribute.AmbientLight)
        if (ambientLight != null) {
            program.setUniformf(
                PawnShaderUniforms.U_AMBIENT_LIGHT,
                ambientLight.color.r,
                ambientLight.color.g,
                ambientLight.color.b
            )
        } else {
            program.setUniformf(PawnShaderUniforms.U_AMBIENT_LIGHT, 0.4f, 0.4f, 0.4f)
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
                    PawnShaderUniforms.U_DIR_LIGHT_COLOR,
                    light.color.r,
                    light.color.g,
                    light.color.b
                )
                program.setUniformf(
                    PawnShaderUniforms.U_DIR_LIGHT_DIR,
                    light.direction.x,
                    light.direction.y,
                    light.direction.z
                )
            }
        } else {
            // Fallback
            program.setUniformf(PawnShaderUniforms.U_DIR_LIGHT_COLOR, 0.8f, 0.8f, 0.8f)
            program.setUniformf(PawnShaderUniforms.U_DIR_LIGHT_DIR, -1f, -0.8f, -0.2f)
        }
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

    private fun setHDRColorUniform(name: PawnShaderUniforms.ColorUniform, color: ColorHDR) {
        program.setUniformf(name.base, color.base.x, color.base.y, color.base.z)
        program.setUniformf(name.intensity, color.intensity)
    }
}

class PawnShaderProvider(
    private val manager: PawnManager,
) : BaseShaderProvider() {

    override fun createShader(renderable: Renderable): Shader {
        return PawnShader(manager, renderable)
    }
}

interface ShaderConfig {
    val skinColor: ColorHDR
    val eyesColor: ColorHDR
    val hairColor: ColorHDR
    val scleraColor: ColorHDR
    val lipsColor: ColorHDR
    val scarsColor: ColorHDR

    // Metal colors
    val metal1Color: ColorHDR
    val metal2Color: ColorHDR
    val metal3Color: ColorHDR

    // Leather colors
    val leather1Color: ColorHDR
    val leather2Color: ColorHDR
    val leather3Color: ColorHDR

    // Cloth colors
    val cloth1Color: ColorHDR
    val cloth2Color: ColorHDR
    val cloth3Color: ColorHDR

    // Gems colors
    val gems1Color: ColorHDR
    val gems2Color: ColorHDR
    val gems3Color: ColorHDR

    // Feathers colors
    val feathers1Color: ColorHDR
    val feathers2Color: ColorHDR
    val feathers3Color: ColorHDR


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
    override var skinColor = ColorHDR(2.02193f, 1.0081f, 0.6199315f)
    override var eyesColor = ColorHDR(0.0734529f, 0.1320755f, 0.05046281f)
    override var hairColor = ColorHDR(0.5943396f, 0.3518379f, 0.1093361f)
    override var scleraColor = ColorHDR(0.9056604f, 0.8159487f, 0.8159487f)
    override var lipsColor = ColorHDR(0.8301887f, 0.3185886f, 0.2780349f)
    override var scarsColor = ColorHDR(0.8490566f, 0.5037117f, 0.3884835f)

    // Metal colors
    override var metal1Color = ColorHDR(2f, 0.682353f, 0.1960784f)
    override var metal2Color = ColorHDR(0.4674706f, 0.4677705f, 0.5188679f)
    override var metal3Color = ColorHDR(0.4383232f, 0.4383232f, 0.4716981f)

    // Leather colors
    override var leather1Color = ColorHDR(0.4811321f, 0.2041155f, 0.08851016f)
    override var leather2Color = ColorHDR(0.4245283f, 0.190437f, 0.09011215f)
    override var leather3Color = ColorHDR(0.1698113f, 0.04637412f, 0.02963688f)//.mul(2.5f)

    // Cloth colors
    override var cloth1Color = ColorHDR(0.1465379f, 0.282117f, 0.3490566f)
    override var cloth2Color = ColorHDR(1f, 0f, 0f)
    override var cloth3Color = ColorHDR(0.8773585f, 0.6337318f, 0.3434941f)

    // Gems colors
    override var gems1Color = ColorHDR(0.3773585f, 0f, 0.06650025f)
    override var gems2Color = ColorHDR(0.2023368f, 0f, 0.4339623f)
    override var gems3Color = ColorHDR(0f, 0.1132075f, 0.01206957f)

    // Feathers colors
    override var feathers1Color = ColorHDR(0.7735849f, 0.492613f, 0.492613f)
    override var feathers2Color = ColorHDR(0.6792453f, 0f, 0f)
    override var feathers3Color = ColorHDR(0f, 0.1793142f, 0.7264151f)


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