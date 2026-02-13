package org.roldy.core.shader

import com.badlogic.gdx.graphics.g3d.Renderable
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.shader.uniform.*
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
    val setter = UniformSetter(program)

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

    private fun <T : UniformValue> UniformAttribute.value(type: String) =
        this.uniforms[type] as? T

    override fun render(renderable: Renderable) {
        val attr = renderable.material.get(UniformAttribute.uniformsAttr) as UniformAttribute
        val leafBaseColor = attr.value<EnvColorUniform>(EnvColorUniform.leafBaseColor)
        val leafNoiseColor = attr.value<EnvColorUniform>(EnvColorUniform.leafNoiseColor)
        val leafNoiseLargeColor = attr.value<EnvColorUniform>(EnvColorUniform.leafNoiseLargeColor)
        val trunkBaseColor = attr.value<EnvColorUniform>(EnvColorUniform.trunkBaseColor)
        val trunkNoiseColor = attr.value<EnvColorUniform>(EnvColorUniform.trunkNoiseColor)
        val useNoiseColor = attr.value<BooleanUniform>(BooleanUniform.useNoiseColor)
        val leafFlatColor = attr.value<BooleanUniform>(BooleanUniform.leafFlatColor)
        val trunkFlatColor = attr.value<BooleanUniform>(BooleanUniform.trunkFlatColor)
        val leafHasNormal = attr.value<BooleanUniform>(BooleanUniform.leafHasNormal)
        val trunkHasNormal = attr.value<BooleanUniform>(BooleanUniform.trunkHasNormal)

        val trunkTexture = attr.value<EnvTextureUniform>(EnvTextureUniform.trunkTexture)
        val leafTexture = attr.value<EnvTextureUniform>(EnvTextureUniform.leafTexture)
        val trunkNormal = attr.value<EnvTextureUniform>(EnvTextureUniform.trunkNormal)
        val leafNormal = attr.value<EnvTextureUniform>(EnvTextureUniform.leafNormal)

        val smallFreq = attr.value<FloatValueUniform>(FloatValueUniform.smallFreq)
        val largeFreq = attr.value<FloatValueUniform>(FloatValueUniform.largeFreq)

        val trunkMetallic = attr.value<FloatValueUniform>(FloatValueUniform.trunkMetallic)
        val leafMetallic = attr.value<FloatValueUniform>(FloatValueUniform.leafMetallic)
        val trunkSmoothness = attr.value<FloatValueUniform>(FloatValueUniform.trunkSmoothness)
        val leafSmoothness = attr.value<FloatValueUniform>(FloatValueUniform.leafSmoothness)

        val leafNormalStrength = attr.value<FloatValueUniform>(FloatValueUniform.leafNormalStrength)
        val trunkNormalStrength = attr.value<FloatValueUniform>(FloatValueUniform.trunkNormalStrength)

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