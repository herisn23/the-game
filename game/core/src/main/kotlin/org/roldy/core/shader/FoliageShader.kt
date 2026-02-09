package org.roldy.core.shader

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Material
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
    private fun createWhiteTexture(): Texture {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
    }

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

    val material: Material = renderable.material
    val leafBaseColor = material.get(FoliageColorAttribute.leafBaseColor) as? FoliageColorAttribute
    val leafNoiseColor = material.get(FoliageColorAttribute.leafNoiseColor) as? FoliageColorAttribute
    val leafNoiseLargeColor = material.get(FoliageColorAttribute.leafNoiseLargeColor) as? FoliageColorAttribute
    val trunkBaseColor = material.get(FoliageColorAttribute.trunkBaseColor) as? FoliageColorAttribute
    val trunkNoiseColor = material.get(FoliageColorAttribute.trunkNoiseColor) as? FoliageColorAttribute
    val useNoiseColor = (material.get(BooleanAttribute.useNoiseColor) as? BooleanAttribute)
    val leafFlatColor = material.get(BooleanAttribute.leafFlatColor) as? BooleanAttribute
    val trunkFlatColor = material.get(BooleanAttribute.trunkFlatColor) as? BooleanAttribute
    val trunkTextureAttr = material.get(FoliageTextureAttribute.trunkTexture) as? FoliageTextureAttribute
    val leafTextureAttr = material.get(FoliageTextureAttribute.leafTexture) as? FoliageTextureAttribute

    val trunkTexture = (trunkTextureAttr?.texture)?.prepare(u_trunkTexture, 20)
    val leafTexture = (leafTextureAttr?.texture)?.prepare(u_leafTexture, 21)

    override fun render(renderable: Renderable) {
        program.setUniformf(u_UVTransform, 0f, 0f, 1f, 1f)

        trunkTexture?.bind()
        leafTexture?.bind()

        leafBaseColor?.set(u_leafBaseColor)
        leafNoiseColor?.set(u_leafNoiseColor)
        leafNoiseLargeColor?.set(u_leafNoiseLargeColor)

        trunkBaseColor?.set(u_trunkBaseColor)
        trunkNoiseColor?.set(u_trunkNoiseColor)

        leafFlatColor?.set(u_leafFlatColor)
        trunkFlatColor?.set(u_trunkFlatColor)

        useNoiseColor?.set(u_useColorNoise)

        program.setUniformf(u_noiseSmallFrequency, smallFrequency)
        program.setUniformf(u_noiseLargeFrequency, largeFrequency)

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

}

class FoliageTextureAttribute(type: Long, val texture: Texture) : Attribute(type) {
    companion object {
        val leafTexture = register("leafTexture")
        val trunkTexture = register("trunkTexture")
        fun createTrunkTexture(texture: Texture) =
            FoliageTextureAttribute(trunkTexture, texture)

        fun createLeafTexture(texture: Texture) =
            FoliageTextureAttribute(leafTexture, texture)
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
    val tree = FoliageColor(hex("4F7B02"), hex("283A05"), hex("6D7D02"))
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