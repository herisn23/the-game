package org.roldy.core.shader

import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Renderable
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.shader.attribute.*
import org.roldy.core.shader.util.ShaderBuilder
import org.roldy.core.shader.util.WindShaderManager
import org.roldy.core.shader.util.fetchUniform
import org.roldy.core.shader.util.shaderProvider
import org.roldy.core.system.WindSystem


class FoliageShader(
    renderable: Renderable,
    config: Config = Config().apply {
        with(ShaderBuilder) {
            vertexShader = ShaderLoader.foliageVert.append(ShaderLoader.windSystem).windFlag(renderable)
            fragmentShader = ShaderLoader.foliageFrag
        }
    },
    offsetProvider: OffsetProvider,
    windSystem: WindSystem
) : WorldShiftingShader(renderable, config, offsetProvider) {
    val windManager = WindShaderManager(windSystem, program)
    val setter = AttributesSetter(program)

    val u_useColorNoise by program.fetchUniform()
    val u_noiseSmallFrequency by program.fetchUniform()
    val u_noiseLargeFrequency by program.fetchUniform()

    val u_leafBaseColor by program.fetchUniform()
    val u_leafNoiseColor by program.fetchUniform()
    val u_leafNoiseLargeColor by program.fetchUniform()
    val u_leafFlatColor by program.fetchUniform()

    val u_trunkBaseColor by program.fetchUniform()
    val u_trunkNoiseColor by program.fetchUniform()
    val u_trunkFlatColor by program.fetchUniform()


    val u_trunkTexture by program.fetchUniform()
    val u_leafTexture by program.fetchUniform()

    val u_trunkNormal by program.fetchUniform()
    val u_trunkHasNormal by program.fetchUniform()

    val u_leafNormal by program.fetchUniform()
    val u_leafHasNormal by program.fetchUniform()

    val u_trunkMetallic by program.fetchUniform()
    val u_leafMetallic by program.fetchUniform()
    val u_trunkSmoothness by program.fetchUniform()
    val u_leafSmoothness by program.fetchUniform()

    val u_leafNormalStrength by program.fetchUniform()
    val u_trunkNormalStrength by program.fetchUniform()

    val u_UVTransform by program.fetchUniform()

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

        with(setter) {
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
        }


        // Set wind uniforms
        windManager.render(renderable)

        super.render(renderable)
    }


}


fun foliageShaderProvider(
    offsetProvider: OffsetProvider,
    windSystem: WindSystem
) =
    shaderProvider {
        FoliageShader(it, offsetProvider = offsetProvider, windSystem = windSystem)
    }