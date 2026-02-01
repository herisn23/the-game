package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Renderable
import org.roldy.core.ColorHDR
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.shader.WorldShiftingShader

class PawnShader(
    val configuration: PawnManager,
    renderable: Renderable
) : WorldShiftingShader(renderable, Config().apply {
    fragmentShader = ShaderLoader.characterFrag
    vertexShader = ShaderLoader.defaultVert
}) {
    // textures

    val u_texture0 by Delegate()
    val u_texture1 by Delegate()
    val u_texture2 by Delegate()
    val u_texture3 by Delegate()
    val u_texture4 by Delegate()
    val u_texture5 by Delegate()
    val u_texture6 by Delegate()
    val u_texture7 by Delegate()

    // smoothness

    val u_skinSmoothness by Delegate()
    val u_eyesSmoothness by Delegate()
    val u_scleraSmoothness by Delegate()
    val u_lipsSmoothness by Delegate()
    val u_hairSmoothness by Delegate()
    val u_scarsSmoothness by Delegate()
    val u_metal1Smoothness by Delegate()
    val u_metal2Smoothness by Delegate()
    val u_metal3Smoothness by Delegate()
    val u_leather1Smoothness by Delegate()
    val u_leather2Smoothness by Delegate()
    val u_leather3Smoothness by Delegate()
    val u_gems1Smoothness by Delegate()
    val u_gems2Smoothness by Delegate()
    val u_gems3Smoothness by Delegate()

    // metallic

    val u_metal1Metallic by Delegate()
    val u_metal2Metallic by Delegate()
    val u_metal3Metallic by Delegate()

    // colors
    val u_feathers1Color by Delegate()
    val u_feathers2Color by Delegate()
    val u_feathers3Color by Delegate()
    val u_feathers1ColorIntensity by Delegate()
    val u_feathers2ColorIntensity by Delegate()
    val u_feathers3ColorIntensity by Delegate()

    val u_gems1Color by Delegate()
    val u_gems2Color by Delegate()
    val u_gems3Color by Delegate()
    val u_gems1ColorIntensity by Delegate()
    val u_gems2ColorIntensity by Delegate()
    val u_gems3ColorIntensity by Delegate()

    val u_cloth1Color by Delegate()
    val u_cloth2Color by Delegate()
    val u_cloth3Color by Delegate()
    val u_cloth1ColorIntensity by Delegate()
    val u_cloth2ColorIntensity by Delegate()
    val u_cloth3ColorIntensity by Delegate()

    val u_leather1Color by Delegate()
    val u_leather2Color by Delegate()
    val u_leather3Color by Delegate()
    val u_leather1ColorIntensity by Delegate()
    val u_leather2ColorIntensity by Delegate()
    val u_leather3ColorIntensity by Delegate()

    val u_metal1Color by Delegate()
    val u_metal2Color by Delegate()
    val u_metal3Color by Delegate()
    val u_metal1ColorIntensity by Delegate()
    val u_metal2ColorIntensity by Delegate()
    val u_metal3ColorIntensity by Delegate()
    val u_scarsColor by Delegate()
    val u_scarsColorIntensity by Delegate()
    val u_lipsColor by Delegate()
    val u_lipsColorIntensity by Delegate()
    val u_scleraColor by Delegate()
    val u_scleraColorIntensity by Delegate()
    val u_hairColor by Delegate()
    val u_hairColorIntensity by Delegate()
    val u_eyesColor by Delegate()
    val u_eyesColorIntensity by Delegate()
    val u_skinColor by Delegate()
    val u_skinColorIntensity by Delegate()


    private val maskTextures = ArmorMaskTextures()
    var bind = 10
    val tex2 = maskTextures.texture2.prepare(u_texture2, bind++)
    val tex0 = maskTextures.texture0.prepare(u_texture0, bind++)
    val tex1 = maskTextures.texture1.prepare(u_texture1, bind++)
    val tex6 = maskTextures.texture6.prepare(u_texture6, bind++)
    val tex5 = maskTextures.texture5.prepare(u_texture5, bind++)
    val tex3 = maskTextures.texture3.prepare(u_texture3, bind++)
    val tex4 = maskTextures.texture4.prepare(u_texture4, bind++)
    val tex7 = maskTextures.texture7.prepare(u_texture7, bind++)

    override fun render(renderable: Renderable) {
        colorize(renderable)
        super.render(renderable)
    }

    fun colorize(renderable: Renderable) {
        val node =
            configuration.instance.meshMap.getValue(with(configuration.instance) { renderable.meshPart.mappingId() })
        val config = configuration.getShaderConfig(node)
        tex0.bind()
        tex1.bind()
        tex2.bind()
        tex3.bind()
        tex4.bind()
        tex5.bind()
        tex6.bind()
        tex7.bind()

        setHDRColorUniform(u_skinColor, u_skinColorIntensity, config.skinColor)
        setHDRColorUniform(u_eyesColor, u_eyesColorIntensity, config.eyesColor)
        setHDRColorUniform(u_scarsColor, u_scarsColorIntensity, config.scarsColor)
        setHDRColorUniform(u_hairColor, u_hairColorIntensity, config.hairColor)
        setHDRColorUniform(u_scleraColor, u_scleraColorIntensity, config.scleraColor)
        setHDRColorUniform(u_lipsColor, u_lipsColorIntensity, config.lipsColor)
        setHDRColorUniform(u_leather1Color, u_leather1ColorIntensity, config.leather1Color)
        setHDRColorUniform(u_leather2Color, u_leather2ColorIntensity, config.leather2Color)
        setHDRColorUniform(u_leather3Color, u_leather3ColorIntensity, config.leather3Color)
        setHDRColorUniform(u_metal1Color, u_metal1ColorIntensity, config.metal1Color)
        setHDRColorUniform(u_metal2Color, u_metal2ColorIntensity, config.metal2Color)
        setHDRColorUniform(u_metal3Color, u_metal3ColorIntensity, config.metal3Color)
        setHDRColorUniform(u_cloth1Color, u_cloth1ColorIntensity, config.cloth1Color)
        setHDRColorUniform(u_cloth2Color, u_cloth2ColorIntensity, config.cloth2Color)
        setHDRColorUniform(u_cloth3Color, u_cloth3ColorIntensity, config.cloth3Color)
        setHDRColorUniform(u_gems1Color, u_gems1ColorIntensity, config.gems1Color)
        setHDRColorUniform(u_gems2Color, u_gems2ColorIntensity, config.gems2Color)
        setHDRColorUniform(u_gems3Color, u_gems3ColorIntensity, config.gems3Color)
        setHDRColorUniform(u_feathers1Color, u_feathers1ColorIntensity, config.feathers1Color)
        setHDRColorUniform(u_feathers2Color, u_feathers2ColorIntensity, config.feathers2Color)
        setHDRColorUniform(u_feathers3Color, u_feathers3ColorIntensity, config.feathers3Color)

        program.setUniformf(u_skinSmoothness, config.skinSmoothness)
        program.setUniformf(u_eyesSmoothness, config.eyesSmoothness)
        program.setUniformf(u_hairSmoothness, config.hairSmoothness)
        program.setUniformf(u_scleraSmoothness, config.scleraSmoothness)
        program.setUniformf(u_lipsSmoothness, config.lipsSmoothness)
        program.setUniformf(u_scarsSmoothness, config.scarsSmoothness)
        program.setUniformf(u_metal1Smoothness, config.metal1Smoothness)
        program.setUniformf(u_metal2Smoothness, config.metal2Smoothness)
        program.setUniformf(u_metal3Smoothness, config.metal3Smoothness)
        program.setUniformf(u_leather1Smoothness, config.leather1Smoothness)
        program.setUniformf(u_leather2Smoothness, config.leather2Smoothness)
        program.setUniformf(u_leather3Smoothness, config.leather3Smoothness)
        program.setUniformf(u_gems1Smoothness, config.gems1Smoothness)
        program.setUniformf(u_gems2Smoothness, config.gems2Smoothness)
        program.setUniformf(u_gems3Smoothness, config.gems3Smoothness)

        program.setUniformf(u_metal1Metallic, config.metal1Metallic)
        program.setUniformf(u_metal2Metallic, config.metal2Metallic)
        program.setUniformf(u_metal3Metallic, config.metal3Metallic)

    }


    private class ArmorMaskTextures {
        val texture0: Texture = PawnAssetManager.mask0.get()
        val texture1: Texture = PawnAssetManager.mask1.get()
        val texture2: Texture = PawnAssetManager.mask2.get()
        val texture3: Texture = PawnAssetManager.mask3.get()
        val texture4: Texture = PawnAssetManager.mask4.get()
        val texture5: Texture = PawnAssetManager.mask5.get()
        val texture6: Texture = PawnAssetManager.mask6.get()
        val texture7: Texture = PawnAssetManager.mask7.get()
    }

    private fun setHDRColorUniform(colorLoc: Int, intensityLoc: Int, color: ColorHDR) {
        program.setUniformf(colorLoc, color.base.x, color.base.y, color.base.z)
        program.setUniformf(intensityLoc, color.intensity)
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