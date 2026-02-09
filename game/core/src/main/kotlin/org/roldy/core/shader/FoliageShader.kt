package org.roldy.core.shader

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.system.WindAttributes


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

    val u_trunkNormal by FetchUniform()
    val u_trunkHasNormal by FetchUniform()

    val u_leafNormal by FetchUniform()
    val u_leafHasNormal by FetchUniform()

    val u_trunkMetallic by FetchUniform()
    val u_leafMetallic by FetchUniform()
    val u_trunkSmoothness by FetchUniform()
    val u_leafSmoothness by FetchUniform()

    val u_leafNormalStrength by FetchUniform()
    val u_trunkNormalStrength by FetchUniform()

    // Wind uniforms
    val u_time by FetchUniform()
    val u_windStrength by FetchUniform()
    val u_windSpeed by FetchUniform()
    val u_windDirection by FetchUniform()

    val u_UVTransform by FetchUniform()
    val u_lightDirection by FetchUniform()

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
        val leafHasNormal = material.value<BooleanAttribute>(BooleanAttribute.leafHasNormal)
        val trunkHasNormal = material.value<BooleanAttribute>(BooleanAttribute.trunkHasNormal)

        val trunkTexture = material.value<FoliageTextureAttribute>(FoliageTextureAttribute.trunkTexture)
        val leafTexture = material.value<FoliageTextureAttribute>(FoliageTextureAttribute.leafTexture)
        val trunkNormal = material.value<FoliageTextureAttribute>(FoliageTextureAttribute.trunkNormal)
        val leafNormal = material.value<FoliageTextureAttribute>(FoliageTextureAttribute.leafNormal)

        val smallFreq = material.value<FloatValueAttribute>(FloatValueAttribute.smallFreq)
        val largeFreq = material.value<FloatValueAttribute>(FloatValueAttribute.largeFreq)

        val trunkMetallic = material.value<FloatValueAttribute>(FloatValueAttribute.trunkMetallic)
        val leafMetallic = material.value<FloatValueAttribute>(FloatValueAttribute.leafMetallic)
        val trunkSmoothness = material.value<FloatValueAttribute>(FloatValueAttribute.trunkSmoothness)
        val leafSmoothness = material.value<FloatValueAttribute>(FloatValueAttribute.leafSmoothness)

        val leafNormalStrength = material.value<FloatValueAttribute>(FloatValueAttribute.leafNormalStrength)
        val trunkNormalStrength = material.value<FloatValueAttribute>(FloatValueAttribute.trunkNormalStrength)

        program.setUniformf(u_UVTransform, 0f, 0f, 1f, 1f)

        trunkTexture?.bind(u_trunkTexture, 20)
        leafTexture?.bind(u_leafTexture, 21)

        trunkNormal?.bind(u_trunkNormal, 22)
        leafNormal?.bind(u_leafNormal, 23)

        leafBaseColor?.set(u_leafBaseColor)
        leafNoiseColor?.set(u_leafNoiseColor)
        leafNoiseLargeColor?.set(u_leafNoiseLargeColor)

        trunkBaseColor?.set(u_trunkBaseColor)
        trunkNoiseColor?.set(u_trunkNoiseColor)

        leafFlatColor?.set(u_leafFlatColor)
        trunkFlatColor?.set(u_trunkFlatColor)

        useNoiseColor?.set(u_useColorNoise)


        smallFreq?.set(u_noiseSmallFrequency)
        largeFreq?.set(u_noiseLargeFrequency)

        trunkMetallic?.set(u_trunkMetallic)
        trunkSmoothness?.set(u_trunkSmoothness)

        leafMetallic?.set(u_leafMetallic)
        leafSmoothness?.set(u_leafSmoothness)

        leafNormalStrength?.set(u_leafNormalStrength)
        trunkNormalStrength?.set(u_trunkNormalStrength)

        leafHasNormal?.set(u_leafHasNormal)
        trunkHasNormal?.set(u_trunkHasNormal)

        // Set wind uniforms
        program.setUniformf(u_time, windAttributes.time)
        program.setUniformf(u_windStrength, windAttributes.windStrength)
        program.setUniformf(u_windSpeed, windAttributes.windSpeed)
        program.setUniformf(u_windDirection, windAttributes.windDirection.x, windAttributes.windDirection.y)

        // Get light direction from environment
        val environment = renderable.environment
        val dirLight = environment?.get(
            DirectionalLightsAttribute::class.java,
            DirectionalLightsAttribute.Type
        ) as? DirectionalLightsAttribute

        if (dirLight != null && dirLight.lights.size > 0) {
            val light = dirLight.lights.first()
            program.setUniformf(u_lightDirection, light.direction.x, light.direction.y, light.direction.z)
        } else {
            // Fallback to default sun direction
            program.setUniformf(u_lightDirection, 0f, -1f, 0f) // Light from above
        }

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

    private fun FloatValueAttribute.set(location: Int) {
        program.setUniformf(location, value)
    }

}

class FoliageTextureAttribute(type: Long, val texture: Texture) : Attribute(type) {
    companion object {
        val leafTexture = register("leafTexture")
        val leafNormal = register("leafNormal")
        val trunkTexture = register("trunkTexture")
        val trunkNormal = register("trunkNormal")

        fun createLeafTexture(texture: Texture) =
            FoliageTextureAttribute(leafTexture, texture)

        fun createTrunkTexture(texture: Texture) =
            FoliageTextureAttribute(trunkTexture, texture)

        fun createLeafNormal(texture: Texture) =
            FoliageTextureAttribute(leafNormal, texture)

        fun createTrunkNormal(texture: Texture) =
            FoliageTextureAttribute(trunkNormal, texture)
    }

    override fun copy() = FoliageTextureAttribute(type, texture)
    override fun compareTo(other: Attribute) = 0
}

class BooleanAttribute(type: Long, val enabled: Boolean) : Attribute(type) {
    companion object {
        val useNoiseColor = register("useNoiseColor")
        val leafFlatColor = register("leafFlatColor")
        val trunkFlatColor = register("trunkFlatColor")
        val leafHasNormal = register("leafHasNormal")
        val trunkHasNormal = register("trunkHasNormal")

        fun createUseNoiseColor(enabled: Boolean) =
            BooleanAttribute(useNoiseColor, enabled)

        fun createLeafFlatColor(enabled: Boolean) =
            BooleanAttribute(leafFlatColor, enabled)

        fun createTrunkFlatColor(enabled: Boolean) =
            BooleanAttribute(trunkFlatColor, enabled)

        fun createLeafHasNormal(enabled: Boolean) =
            BooleanAttribute(leafHasNormal, enabled)

        fun createTrunkHasNormal(enabled: Boolean) =
            BooleanAttribute(trunkHasNormal, enabled)
    }

    override fun copy() = BooleanAttribute(type, enabled)
    override fun compareTo(other: Attribute) = 0
    val asFloat = if (enabled) 1.0f else 0.0f
}


class FloatValueAttribute(type: Long, val value: Float) : Attribute(type) {
    companion object {
        val smallFreq = register("smallFreq")
        val largeFreq = register("largeFreq")
        val leafMetallic = register("leafMetallic")
        val leafSmoothness = register("leafSmoothness")
        val trunkMetallic = register("trunkMetallic")
        val trunkSmoothness = register("trunkSmoothness")
        val leafNormalStrength = register("leafNormalStrength")
        val trunkNormalStrength = register("trunkNormalStrength")

        fun createSmallFreq(freq: Float) = FloatValueAttribute(smallFreq, freq)
        fun createLargeFreq(freq: Float) = FloatValueAttribute(largeFreq, freq)
        fun createLeafMetallic(freq: Float) = FloatValueAttribute(leafMetallic, freq)
        fun createLeafSmoothness(freq: Float) = FloatValueAttribute(leafSmoothness, freq)
        fun createTrunkMetallic(freq: Float) = FloatValueAttribute(trunkMetallic, freq)
        fun createTrunkSmoothness(freq: Float) = FloatValueAttribute(trunkSmoothness, freq)
        fun createLeafNormalStrength(freq: Float) = FloatValueAttribute(leafNormalStrength, freq)
        fun createTrunkNormalStrength(freq: Float) = FloatValueAttribute(trunkNormalStrength, freq)
    }

    override fun copy(): Attribute {
        return FloatValueAttribute(type, value)
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

fun foliageShaderProvider(
    offsetProvider: OffsetProvider,
    windAttributes: WindAttributes
) =
    shaderProvider {
        FoliageShader(it, offsetProvider = offsetProvider, windAttributes = windAttributes)
    }