package org.roldy.core.shader

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.system.WindAttributes
import org.roldy.core.utils.hex

class FoliageShader(
    renderable: Renderable,
    config: Config = Config().apply {
        with(ShaderBuilder) {
            vertexShader = ShaderLoader.foliageVert.windFlag(renderable)
            fragmentShader = ShaderLoader.foliageFrag
        }
    },
    offsetProvider: OffsetProvider,
    private val windAttributes: WindAttributes
) : WorldShiftingShader(renderable, config, offsetProvider) {

    val smallFrequency: Float = 0.2f
    val largeFrequency: Float = 0.1f

    val u_noiseSmallFrequency by FetchUniform()
    val u_noiseLargeFrequency by FetchUniform()

    val u_baseColor by FetchUniform()
    val u_noiseColor by FetchUniform()
    val u_noiseLargeColor by FetchUniform()

    // Wind uniforms
    val u_time by FetchUniform()
    val u_windStrength by FetchUniform()
    val u_windSpeed by FetchUniform()
    val u_windDirection by FetchUniform()

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

        program.setUniformf(u_noiseSmallFrequency, smallFrequency)
        program.setUniformf(u_noiseLargeFrequency, largeFrequency)

        // Set wind uniforms
        program.setUniformf(u_time, windAttributes.time)
        program.setUniformf(u_windStrength, windAttributes.windStrength)
        program.setUniformf(u_windSpeed, windAttributes.windSpeed)
        program.setUniformf(u_windDirection, windAttributes.windDirection.x, windAttributes.windDirection.y)

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
    windAttributes: WindAttributes
) =
    shaderProvider {
        FoliageShader(it, offsetProvider = offsetProvider, windAttributes = windAttributes)
    }