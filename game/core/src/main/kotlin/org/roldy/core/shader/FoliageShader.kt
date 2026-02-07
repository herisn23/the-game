package org.roldy.core.shader

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.utils.hex

data class FoliageNoise(
    var smallFrequency: Float = 0.2f,
    var largeFrequency: Float = 0.1f
)

class FoliageShader(
    renderable: Renderable,
    config: Config = Config().apply {
        vertexShader = ShaderLoader.foliageVert
        fragmentShader = ShaderLoader.foliageFrag
    },
    offsetProvider: OffsetProvider,
    val noise: FoliageNoise
) : WorldShiftingShader(renderable, config, offsetProvider) {

    // Toggle flat color mode (0.0 = textured, 1.0 = flat)
    val useFlatColor = 1f

    // Toggle color noise (0.0 = off, 1.0 = on)
    val useColorNoise = 1f

    val u_noiseSmallFrequency by FetchUniform()
    val u_noiseLargeFrequency by FetchUniform()

    val u_baseColor by FetchUniform()
    val u_noiseColor by FetchUniform()
    val u_noiseLargeColor by FetchUniform()

    val u_leafFlatColor by FetchUniform()
    val u_useColorNoise by FetchUniform()

    val baseColor = renderable.material.get(FoliageColorAttribute.baseColor) as? FoliageColorAttribute
    val noiseColor = renderable.material.get(FoliageColorAttribute.noiseColor) as? FoliageColorAttribute
    val noiseLargeColor = renderable.material.get(FoliageColorAttribute.noiseLargeColor) as? FoliageColorAttribute


    override fun render(renderable: Renderable) {
        baseColor?.run {
            program.setUniformf(u_baseColor, color.r, color.g, color.b)
        }
        noiseColor?.run {
            program.setUniformf(u_noiseColor, color.r, color.g, color.b)
        }
        noiseLargeColor?.run {
            program.setUniformf(u_noiseLargeColor, color.r, color.g, color.b)
        }

        program.setUniformf(u_noiseSmallFrequency, noise.smallFrequency)
        program.setUniformf(u_noiseLargeFrequency, noise.largeFrequency)

        program.setUniformf(u_leafFlatColor, useFlatColor)
        program.setUniformf(u_useColorNoise, useColorNoise)

//        program.setUniformf(u_baseColor, 1f, 1f, 1f)  // White
//        program.setUniformf(u_noiseColor, 1f, 1f, 1f)
//        program.setUniformf(u_noiseLargeColor, 1f, 1f, 1f)
//        program.setUniformf(u_leafFlatColor, 0f)  // FALSE
//        program.setUniformf(u_useColorNoise, 0f)  // FALSE - disable noise

        super.render(renderable)
    }
}

class FoliageColorAttribute(type: Long, val color: Color) : Attribute(type) {

    companion object {
        val baseColor = register("baseColor")
        val noiseColor = register("noiseColor")
        val noiseLargeColor = register("noiseLargeColor")

        fun createBaseColor(color: Color) =
            FoliageColorAttribute(baseColor, color)

        fun createNoiseColor(color: Color) =
            FoliageColorAttribute(noiseColor, color)

        fun createNoiseLargeColor(color: Color) =
            FoliageColorAttribute(noiseLargeColor, color)
    }

    override fun copy(): Attribute {
        return FoliageColorAttribute(type, color)
    }

    override fun compareTo(other: Attribute): Int {
        if (type != other.type) return (type - other.type).toInt()
        return (other as ColorAttribute).color.toIntBits() - color.toIntBits()
    }
}


data class FoliageColor(
    val base: Color,
    val noise: Color,
    val noiseLarge: Color
)

object FoliageColors {
    val grass = FoliageColor(hex("1A4800"), hex("8FA100"), hex("484400"))
}


fun foliageShaderProvider(
    offsetProvider: OffsetProvider,
    noise: FoliageNoise = FoliageNoise()
) =
    shaderProvider {
        FoliageShader(it, offsetProvider = offsetProvider, noise = noise)
    }