package org.roldy.core.material.foliage

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import org.roldy.core.asset.Asset
import org.roldy.core.biome.BiomeType
import org.roldy.core.configuration.loadFoliageMaterialConfiguration
import org.roldy.core.shader.BooleanAttribute
import org.roldy.core.shader.FloatValueAttribute
import org.roldy.core.shader.FoliageColorAttribute
import org.roldy.core.shader.FoliageTextureAttribute

fun loadMaterials(
    textures: Map<String, Asset<Texture>>
): List<Material> =
    loadFoliageMaterialConfiguration(BiomeType.Tropical).materials.map { it.toMaterial(textures) }


fun FoliageConfiguration.toMaterial(
    textures: Map<String, Asset<Texture>>
): Material =
    Material().apply {
        id = this@toMaterial.id
        set(BooleanAttribute.createTrunkFlatColor(false))
        set(BooleanAttribute.createUseNoiseColor(useColorNoise))
        set(FloatValueAttribute.createSmallFreq(smallNoiseFreq))
        set(FloatValueAttribute.createLargeFreq(largeNoiseFreq))
//        set(ColorAttribute.createSpecular(hex("808080")))

        leaves?.let {
            set(BooleanAttribute.createLeafFlatColor(it.useFlatColor))
            set(FoliageColorAttribute.createLeafBaseColor(it.baseColor))
            set(FoliageColorAttribute.createLeafNoiseColor(it.noiseColor))
            set(FoliageColorAttribute.createLeafNoiseLargeColor(it.noiseLargeColor))
            set(FloatValueAttribute.createLeafMetallic(it.metallic))
            set(FloatValueAttribute.createLeafSmoothness(it.smoothness))
            set(FloatValueAttribute.createLeafNormalStrength(it.normalStrength))

            textures.pick(it.texture) {
                set(FoliageTextureAttribute.createLeafTexture(this))
                set(TextureAttribute.createDiffuse(this)) // needed for alpha clipping on shadows
            }
            textures.pick(it.normal) {
                set(BooleanAttribute.createLeafHasNormal(true))
                set(FoliageTextureAttribute.createLeafNormal(this))
            }
        }

        trunk?.let {
            set(BooleanAttribute.createTrunkFlatColor(it.useFlatColor))
            set(FoliageColorAttribute.createTrunkBaseColor(it.baseColor))
            set(FoliageColorAttribute.createTrunkNoiseColor(it.noiseColor))
            set(FloatValueAttribute.createTrunkMetallic(it.metallic))
            set(FloatValueAttribute.createTrunkSmoothness(it.smoothness))
            set(FloatValueAttribute.createTrunkNormalStrength(it.normalStrength))

            textures.pick(it.texture) {
                set(FoliageTextureAttribute.createTrunkTexture(this))
            }
            textures.pick(it.normal) {
                set(BooleanAttribute.createTrunkHasNormal(true))
                set(FoliageTextureAttribute.createTrunkNormal(this))
            }
        }

        set(IntAttribute.createCullFace(GL20.GL_NONE))
        set(FloatAttribute.createAlphaTest(0.8f))
    }

private fun Map<String, Asset<Texture>>.pick(name: String?, onfound: Texture.() -> Unit) {
    this[name]?.get()?.onfound()
}