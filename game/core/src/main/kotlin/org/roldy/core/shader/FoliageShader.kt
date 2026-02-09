package org.roldy.core.shader

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Renderable
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
    val smallFrequency: Float = 10f
    val largeFrequency: Float = 0.1f

    val u_useColorNoise by FetchUniform()
    val u_noiseSmallFrequency by FetchUniform()
    val u_noiseLargeFrequency by FetchUniform()

    val u_leafBaseColor by FetchUniform()
    val u_leafNoiseColor by FetchUniform()
    val u_leafNoiseLargeColor by FetchUniform()
    val u_leafFlatColor by FetchUniform()

    val u_trunkBaseColor by FetchUniform()
    val u_trunkNoiseColor by FetchUniform()
    val u_trunkFlatColor by FetchUniform()


    val u_trunkTexture by FetchUniform()
    val u_leafTexture by FetchUniform()

    // Wind uniforms
    val u_time by FetchUniform()
    val u_windStrength by FetchUniform()
    val u_windSpeed by FetchUniform()
    val u_windDirection by FetchUniform()

    val u_UVTransform by FetchUniform()

    private fun <T : Attribute> Material.value(type: Long) =
        get(type) as? T

    override fun render(renderable: Renderable) {
        val material: Material = renderable.material
        val leafBaseColor = material.value<FoliageColorAttribute>(FoliageColorAttribute.leafBaseColor)
        val leafNoiseColor = material.value<FoliageColorAttribute>(FoliageColorAttribute.leafNoiseColor)
        val leafNoiseLargeColor = material.value<FoliageColorAttribute>(FoliageColorAttribute.leafNoiseLargeColor)
        val trunkBaseColor = material.value<FoliageColorAttribute>(FoliageColorAttribute.trunkBaseColor)
        val trunkNoiseColor = material.value<FoliageColorAttribute>(FoliageColorAttribute.trunkNoiseColor)
        val useNoiseColor = material.value<BooleanAttribute>(BooleanAttribute.useNoiseColor)
        val leafFlatColor = material.value<BooleanAttribute>(BooleanAttribute.leafFlatColor)
        val trunkFlatColor = material.value<BooleanAttribute>(BooleanAttribute.trunkFlatColor)
        val trunkTexture = material.value<FoliageTextureAttribute>(FoliageTextureAttribute.trunkTexture)
        val leafTexture = material.value<FoliageTextureAttribute>(FoliageTextureAttribute.leafTexture)
        val smallFreq = material.value<NoiseFreqAttribute>(NoiseFreqAttribute.smallFreq)?.freq ?: smallFrequency
        val largeFreq = material.value<NoiseFreqAttribute>(NoiseFreqAttribute.largeFreq)?.freq ?: largeFrequency

        program.setUniformf(u_UVTransform, 0f, 0f, 1f, 1f)

        trunkTexture?.bind(u_trunkTexture, 20)
        leafTexture?.bind(u_leafTexture, 21)

        leafBaseColor?.set(u_leafBaseColor)
        leafNoiseColor?.set(u_leafNoiseColor)
        leafNoiseLargeColor?.set(u_leafNoiseLargeColor)

        trunkBaseColor?.set(u_trunkBaseColor)
        trunkNoiseColor?.set(u_trunkNoiseColor)

        leafFlatColor?.set(u_leafFlatColor)
        trunkFlatColor?.set(u_trunkFlatColor)

        useNoiseColor?.set(u_useColorNoise)

        program.setUniformf(u_noiseSmallFrequency, smallFreq)
        program.setUniformf(u_noiseLargeFrequency, largeFreq)

        // Set wind uniforms
        program.setUniformf(u_time, windAttributes.time)
        program.setUniformf(u_windStrength, windAttributes.windStrength)
        program.setUniformf(u_windSpeed, windAttributes.windSpeed)
        program.setUniformf(u_windDirection, windAttributes.windDirection.x, windAttributes.windDirection.y)

        super.render(renderable)
    }

    private fun FoliageColorAttribute.set(location: Int) {
        program.setUniformf(location, color.r, color.g, color.b)
    }

    private fun BooleanAttribute.set(location: Int) {
        program.setUniformf(location, asFloat)
    }

    private fun FoliageTextureAttribute.bind(location: Int, bind: Int) {
        texture.bind(bind)
        program.setUniformi(location, bind)
    }

}

class FoliageTextureAttribute(type: Long, val texture: Texture) : Attribute(type) {
    companion object {
        val leafTexture = register("leafTexture")
        val trunkTexture = register("trunkTexture")

        fun createLeafTexture(texture: Texture) =
            FoliageTextureAttribute(leafTexture, texture)

        fun createTrunkTexture(texture: Texture) =
            FoliageTextureAttribute(trunkTexture, texture)
    }

    override fun copy() = FoliageTextureAttribute(type, texture)
    override fun compareTo(other: Attribute) = 0
}

class BooleanAttribute(type: Long, val enabled: Boolean) : Attribute(type) {
    companion object {
        val useNoiseColor = register("useNoiseColor")
        val leafFlatColor = register("leafFlatColor")
        val trunkFlatColor = register("trunkFlatColor")

        fun createUseNoiseColor(enabled: Boolean) =
            BooleanAttribute(useNoiseColor, enabled)

        fun createLeafFlatColor(enabled: Boolean) =
            BooleanAttribute(leafFlatColor, enabled)

        fun createTrunkFlatColor(enabled: Boolean) =
            BooleanAttribute(trunkFlatColor, enabled)
    }

    override fun copy() = BooleanAttribute(type, enabled)
    override fun compareTo(other: Attribute) = 0
    val asFloat = if (enabled) 1.0f else 0.0f
}

class NoiseFreqAttribute(type: Long, val freq: Float) : Attribute(type) {
    companion object {
        val smallFreq = register("smallFreq")
        val largeFreq = register("largeFreq")

        fun createSmallFreq(freq: Float) = NoiseFreqAttribute(smallFreq, freq)
        fun createLargeFreq(freq: Float) = NoiseFreqAttribute(largeFreq, freq)
    }

    override fun copy(): Attribute {
        return NoiseFreqAttribute(type, freq)
    }

    override fun compareTo(other: Attribute): Int = 0
}

class FoliageColorAttribute(type: Long, val color: Color) : Attribute(type) {

    companion object {
        val leafBaseColor = register("leafBaseColor")
        val leafNoiseColor = register("leafNoiseColor")
        val leafNoiseLargeColor = register("leafNoiseLargeColor")


        val trunkBaseColor = register("trunkBaseColor")
        val trunkNoiseColor = register("trunkNoiseColor")

        fun createLeafBaseColor(color: Color) =
            FoliageColorAttribute(leafBaseColor, color)

        fun createLeafNoiseColor(color: Color) =
            FoliageColorAttribute(leafNoiseColor, color)

        fun createLeafNoiseLargeColor(color: Color) =
            FoliageColorAttribute(leafNoiseLargeColor, color)

        fun createTrunkBaseColor(color: Color) =
            FoliageColorAttribute(trunkBaseColor, color)

        fun createTrunkNoiseColor(color: Color) =
            FoliageColorAttribute(trunkNoiseColor, color)
    }

    override fun copy(): Attribute {
        return FoliageColorAttribute(type, color)
    }

    override fun compareTo(other: Attribute): Int = 0
}


data class FoliageColor(
    val base: Color,
    val noise: Color,
    val noiseLarge: Color
)

object FoliageColors {
    val grass = FoliageColor(hex("1A4800"), hex("8FA100"), hex("484400"))
    val tree = FoliageColor(hex("4F7B02"), hex("283A05"), hex("6D7D02"))
    val palm = FoliageColor(hex("FFFFFF"), hex("D0FFA2"), hex("FFC677"))
    val test = FoliageColor(hex("ff0000"), hex("00ff00"), hex("0000ff"))
    val white = FoliageColor(hex("ffffff"), hex("ffffff"), hex("ffffff"))
}


fun foliageShaderProvider(
    offsetProvider: OffsetProvider,
    windAttributes: WindAttributes
) =
    shaderProvider {
        FoliageShader(it, offsetProvider = offsetProvider, windAttributes = windAttributes)
    }