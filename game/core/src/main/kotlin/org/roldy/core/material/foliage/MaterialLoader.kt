package org.roldy.core.material.foliage

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import org.roldy.core.asset.Asset
import org.roldy.core.biome.BiomeType
import org.roldy.core.configuration.loadFoliageMaterialConfiguration

fun loadMaterials(
    textures: Map<String, Asset<Texture>>
): Map<BiomeType, Material> =
    mapOf(
        BiomeType.Tropical.let {
            it to loadFoliageMaterialConfiguration(it).toMaterial()
        }
    )


fun FoliageMaterialConfiguration.toMaterial(): Material =
    Material().apply {

    }