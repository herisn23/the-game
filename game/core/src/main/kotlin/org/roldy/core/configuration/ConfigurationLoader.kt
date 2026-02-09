package org.roldy.core.configuration

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import org.roldy.core.asset.loadAsset
import org.roldy.core.biome.BiomeType
import org.roldy.core.biome.BiomesConfiguration
import org.roldy.core.material.foliage.FoliageMaterialConfiguration


private fun readConfigurationFile(file: String) =
    loadAsset(file).readString()

inline fun <reified T> String.decode() =
    Yaml.default.decodeFromString<T>(this)

fun loadBiomesConfiguration(): BiomesConfiguration =
    readConfigurationFile("biomes-configuration.yaml").decode()


val foliageConfiguration = mapOf(
    BiomeType.Tropical to "tropic-material-config.yaml"
)

fun loadFoliageMaterialConfiguration(biome: BiomeType): FoliageMaterialConfiguration =
    readConfigurationFile(foliageConfiguration.getValue(biome)).decode()
